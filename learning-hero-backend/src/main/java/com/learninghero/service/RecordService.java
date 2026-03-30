package com.learninghero.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.learninghero.common.BusinessException;
import com.learninghero.common.ErrorCode;
import com.learninghero.dto.response.PageResponse;
import com.learninghero.dto.response.QuestionDetailVO;
import com.learninghero.dto.response.StudyRecordVO;
import com.learninghero.dto.response.WrongAnswerVO;
import com.learninghero.entity.AnswerDetail;
import com.learninghero.entity.Question;
import com.learninghero.entity.StudyRecord;
import com.learninghero.entity.WrongAnswer;
import com.learninghero.mapper.AnswerDetailMapper;
import com.learninghero.mapper.QuestionMapper;
import com.learninghero.mapper.StudyRecordMapper;
import com.learninghero.mapper.WrongAnswerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final StudyRecordMapper studyRecordMapper;
    private final AnswerDetailMapper answerDetailMapper;
    private final WrongAnswerMapper wrongAnswerMapper;
    private final QuestionMapper questionMapper;

    public PageResponse<StudyRecordVO> getStudyHistory(Long userId, int page, int size) {
        Page<StudyRecord> pageParam = new Page<>(page, size);
        Page<StudyRecord> recordPage = studyRecordMapper.selectPage(
                pageParam,
                new LambdaQueryWrapper<StudyRecord>()
                        .eq(StudyRecord::getUserId, userId)
                        .orderByDesc(StudyRecord::getCreatedAt)
        );

        PageResponse<StudyRecordVO> response = new PageResponse<>();
        response.setTotal(recordPage.getTotal());
        response.setPage(page);
        response.setSize(size);
        response.setRecords(recordPage.getRecords().stream()
                .map(this::convertToVO)
                .toList());

        return response;
    }

    public StudyRecordVO getStudyRecordDetail(Long userId, Long recordId) {
        StudyRecord record = studyRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException(ErrorCode.RECORD_NOT_FOUND);
        }

        if (!record.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权查看此记录");
        }

        return convertToVO(record);
    }

    @Transactional
    public void deleteStudyRecord(Long userId, Long recordId) {
        StudyRecord record = studyRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException(ErrorCode.RECORD_NOT_FOUND);
        }

        if (!record.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权删除此记录");
        }

        answerDetailMapper.delete(
                new LambdaQueryWrapper<AnswerDetail>().eq(AnswerDetail::getRecordId, recordId)
        );
        studyRecordMapper.deleteById(recordId);
    }

    public PageResponse<WrongAnswerVO> getWrongAnswers(Long userId, int page, int size) {
        Page<WrongAnswer> pageParam = new Page<>(page, size);
        Page<WrongAnswer> wrongPage = wrongAnswerMapper.selectPage(
                pageParam,
                new LambdaQueryWrapper<WrongAnswer>()
                        .eq(WrongAnswer::getUserId, userId)
                        .orderByDesc(WrongAnswer::getLastWrongAt)
        );

        PageResponse<WrongAnswerVO> response = new PageResponse<>();
        response.setTotal(wrongPage.getTotal());
        response.setPage(page);
        response.setSize(size);
        response.setRecords(wrongPage.getRecords().stream()
                .map(this::convertToWrongAnswerVO)
                .toList());

        return response;
    }

    @Transactional
    public void removeWrongAnswer(Long userId, Long wrongAnswerId) {
        WrongAnswer wrongAnswer = wrongAnswerMapper.selectById(wrongAnswerId);
        if (wrongAnswer == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "错题记录不存在");
        }

        if (!wrongAnswer.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权删除此错题");
        }

        wrongAnswerMapper.deleteById(wrongAnswerId);
    }

    private StudyRecordVO convertToVO(StudyRecord record) {
        StudyRecordVO vo = new StudyRecordVO();
        vo.setId(record.getId());
        vo.setTopic(record.getTopic());
        vo.setDifficulty(record.getDifficulty());
        vo.setTotalQuestions(record.getTotalQuestions());
        vo.setCorrectCount(record.getCorrectCount());
        vo.setScore(record.getScore());
        vo.setDuration(record.getDuration());
        vo.setCreatedAt(record.getCreatedAt());
        return vo;
    }

    private WrongAnswerVO convertToWrongAnswerVO(WrongAnswer wrongAnswer) {
        WrongAnswerVO vo = new WrongAnswerVO();
        vo.setId(wrongAnswer.getId());
        vo.setWrongCount(wrongAnswer.getWrongCount());
        vo.setLastWrongAt(wrongAnswer.getLastWrongAt());

        Question question = questionMapper.selectById(wrongAnswer.getQuestionId());
        if (question != null) {
            QuestionDetailVO questionVO = new QuestionDetailVO();
            questionVO.setId(question.getId());
            questionVO.setTopic(question.getTopic());
            questionVO.setQuestion(question.getQuestion());
            questionVO.setOptions(Arrays.asList(
                    question.getOptionA(),
                    question.getOptionB(),
                    question.getOptionC(),
                    question.getOptionD()
            ));
            questionVO.setAnswer(question.getAnswer());
            questionVO.setExplanation(question.getExplanation());
            vo.setQuestion(questionVO);
        }

        return vo;
    }
}
