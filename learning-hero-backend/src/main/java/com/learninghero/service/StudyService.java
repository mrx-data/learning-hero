package com.learninghero.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.learninghero.common.BusinessException;
import com.learninghero.common.ErrorCode;
import com.learninghero.config.BusinessConfig;
import com.learninghero.dto.request.EndSessionRequest;
import com.learninghero.dto.request.GenerateQuestionsRequest;
import com.learninghero.dto.request.SubmitAnswerRequest;
import com.learninghero.dto.response.*;
import com.learninghero.entity.AnswerDetail;
import com.learninghero.entity.Question;
import com.learninghero.entity.StudyRecord;
import com.learninghero.entity.WrongAnswer;
import com.learninghero.mapper.AnswerDetailMapper;
import com.learninghero.mapper.QuestionMapper;
import com.learninghero.mapper.StudyRecordMapper;
import com.learninghero.mapper.WrongAnswerMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudyService {

    private final AiService aiService;
    private final UserService userService;
    private final QuestionMapper questionMapper;
    private final StudyRecordMapper studyRecordMapper;
    private final AnswerDetailMapper answerDetailMapper;
    private final WrongAnswerMapper wrongAnswerMapper;
    private final BusinessConfig businessConfig;

    private final Map<String, StudySession> sessionMap = new ConcurrentHashMap<>();

    @Transactional
    public GenerateQuestionsResponse generateQuestions(Long userId, GenerateQuestionsRequest request) {
        List<Question> questions = aiService.generateQuestions(
                request.getTopic(),
                request.getDifficulty(),
                request.getCount()
        );

        for (Question question : questions) {
            questionMapper.insert(question);
        }

        String sessionId = UUID.randomUUID().toString().replace("-", "");
        StudySession session = new StudySession();
        session.setSessionId(sessionId);
        session.setUserId(userId);
        session.setTopic(request.getTopic());
        session.setDifficulty(request.getDifficulty());
        session.setQuestions(questions);
        session.setCurrentIndex(0);
        session.setScore(0);
        session.setCorrectCount(0);
        session.setStartTime(System.currentTimeMillis());
        session.setAnswerDetails(new ArrayList<>());

        sessionMap.put(sessionId, session);

        List<QuestionVO> questionVOs = new ArrayList<>();
        for (Question q : questions) {
            QuestionVO vo = new QuestionVO();
            vo.setId(q.getId());
            vo.setQuestion(q.getQuestion());
            vo.setOptions(Arrays.asList(q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD()));
            vo.setDifficulty(q.getDifficulty());
            questionVOs.add(vo);
        }

        GenerateQuestionsResponse response = new GenerateQuestionsResponse();
        response.setSessionId(sessionId);
        response.setQuestions(questionVOs);
        response.setTotalCount(questions.size());

        return response;
    }

    @Transactional
    public SubmitAnswerResponse submitAnswer(Long userId, SubmitAnswerRequest request) {
        StudySession session = sessionMap.get(request.getSessionId());
        if (session == null) {
            throw new BusinessException(ErrorCode.SESSION_NOT_FOUND);
        }

        if (!session.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权操作此会话");
        }

        Question question = session.getQuestions().stream()
                .filter(q -> q.getId().equals(request.getQuestionId()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));

        boolean isCorrect = question.getAnswer().equals(request.getAnswer());

        AnswerDetail detail = new AnswerDetail();
        detail.setQuestionId(question.getId());
        detail.setUserAnswer(request.getAnswer());
        detail.setIsCorrect(isCorrect ? 1 : 0);
        detail.setAnswerTime(request.getAnswerTime());
        session.getAnswerDetails().add(detail);

        if (isCorrect) {
            session.setCorrectCount(session.getCorrectCount() + 1);
            int score = getScoreByDifficulty(session.getDifficulty());
            session.setScore(session.getScore() + score);
        } else {
            WrongAnswer wrongAnswer = wrongAnswerMapper.selectOne(
                    new LambdaQueryWrapper<WrongAnswer>()
                            .eq(WrongAnswer::getUserId, userId)
                            .eq(WrongAnswer::getQuestionId, question.getId())
            );

            if (wrongAnswer == null) {
                wrongAnswer = new WrongAnswer();
                wrongAnswer.setUserId(userId);
                wrongAnswer.setQuestionId(question.getId());
                wrongAnswer.setWrongCount(1);
                wrongAnswer.setLastWrongAt(LocalDateTime.now());
                wrongAnswerMapper.insert(wrongAnswer);
            } else {
                wrongAnswer.setWrongCount(wrongAnswer.getWrongCount() + 1);
                wrongAnswer.setLastWrongAt(LocalDateTime.now());
                wrongAnswerMapper.updateById(wrongAnswer);
            }
        }

        session.setCurrentIndex(session.getCurrentIndex() + 1);

        SubmitAnswerResponse response = new SubmitAnswerResponse();
        response.setIsCorrect(isCorrect);
        response.setCorrectAnswer(question.getAnswer());
        response.setExplanation(question.getExplanation());
        response.setCurrentScore(session.getScore());
        response.setCurrentIndex(session.getCurrentIndex());
        response.setTotalCount(session.getQuestions().size());

        return response;
    }

    @Transactional
    public StudyResultResponse endSession(Long userId, EndSessionRequest request) {
        StudySession session = sessionMap.get(request.getSessionId());
        if (session == null) {
            throw new BusinessException(ErrorCode.SESSION_NOT_FOUND);
        }

        if (!session.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权操作此会话");
        }

        int duration = (int) ((System.currentTimeMillis() - session.getStartTime()) / 1000);

        StudyRecord record = new StudyRecord();
        record.setUserId(userId);
        record.setTopic(session.getTopic());
        record.setDifficulty(session.getDifficulty());
        record.setTotalQuestions(session.getQuestions().size());
        record.setCorrectCount(session.getCorrectCount());
        record.setScore(session.getScore());
        record.setDuration(duration);
        studyRecordMapper.insert(record);

        for (AnswerDetail detail : session.getAnswerDetails()) {
            detail.setRecordId(record.getId());
            answerDetailMapper.insert(detail);
        }

        userService.updateStudyStats(userId, session.getQuestions().size(), session.getCorrectCount());

        sessionMap.remove(request.getSessionId());

        StudyResultResponse response = new StudyResultResponse();
        response.setRecordId(record.getId());
        response.setScore(session.getScore());
        response.setTotalQuestions(session.getQuestions().size());
        response.setCorrectCount(session.getCorrectCount());
        response.setAccuracy((double) session.getCorrectCount() / session.getQuestions().size() * 100);
        response.setDuration(duration);
        response.setAnswers(buildAnswerDetailVOs(session));

        return response;
    }

    public StudyResultResponse getResult(Long userId, Long recordId) {
        StudyRecord record = studyRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException(ErrorCode.RECORD_NOT_FOUND);
        }

        if (!record.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权查看此记录");
        }

        List<AnswerDetail> details = answerDetailMapper.selectList(
                new LambdaQueryWrapper<AnswerDetail>().eq(AnswerDetail::getRecordId, recordId)
        );

        StudyResultResponse response = new StudyResultResponse();
        response.setRecordId(record.getId());
        response.setScore(record.getScore());
        response.setTotalQuestions(record.getTotalQuestions());
        response.setCorrectCount(record.getCorrectCount());
        response.setAccuracy((double) record.getCorrectCount() / record.getTotalQuestions() * 100);
        response.setDuration(record.getDuration());
        response.setAnswers(buildAnswerDetailVOsFromDetails(details));

        return response;
    }

    private int getScoreByDifficulty(String difficulty) {
        if (difficulty == null) {
            return businessConfig.getDifficulty().getMediumScore();
        }
        return switch (difficulty.toLowerCase()) {
            case "easy" -> businessConfig.getDifficulty().getEasyScore();
            case "hard" -> businessConfig.getDifficulty().getHardScore();
            default -> businessConfig.getDifficulty().getMediumScore();
        };
    }

    private List<AnswerDetailVO> buildAnswerDetailVOs(StudySession session) {
        List<AnswerDetailVO> result = new ArrayList<>();
        for (int i = 0; i < session.getQuestions().size(); i++) {
            Question q = session.getQuestions().get(i);
            AnswerDetail d = i < session.getAnswerDetails().size() ? session.getAnswerDetails().get(i) : null;

            AnswerDetailVO vo = new AnswerDetailVO();
            vo.setQuestionId(q.getId());
            vo.setQuestion(q.getQuestion());
            vo.setOptions(Arrays.asList(q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD()));
            vo.setCorrectAnswer(q.getAnswer());
            vo.setExplanation(q.getExplanation());

            if (d != null) {
                vo.setUserAnswer(d.getUserAnswer());
                vo.setIsCorrect(d.getIsCorrect() == 1);
            }

            result.add(vo);
        }
        return result;
    }

    private List<AnswerDetailVO> buildAnswerDetailVOsFromDetails(List<AnswerDetail> details) {
        List<AnswerDetailVO> result = new ArrayList<>();
        for (AnswerDetail d : details) {
            Question q = questionMapper.selectById(d.getQuestionId());

            AnswerDetailVO vo = new AnswerDetailVO();
            vo.setQuestionId(q.getId());
            vo.setQuestion(q.getQuestion());
            vo.setOptions(Arrays.asList(q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD()));
            vo.setCorrectAnswer(q.getAnswer());
            vo.setExplanation(q.getExplanation());
            vo.setUserAnswer(d.getUserAnswer());
            vo.setIsCorrect(d.getIsCorrect() == 1);

            result.add(vo);
        }
        return result;
    }

    @lombok.Data
    private static class StudySession {
        private String sessionId;
        private Long userId;
        private String topic;
        private String difficulty;
        private List<Question> questions;
        private int currentIndex;
        private int score;
        private int correctCount;
        private long startTime;
        private List<AnswerDetail> answerDetails;
    }
}
