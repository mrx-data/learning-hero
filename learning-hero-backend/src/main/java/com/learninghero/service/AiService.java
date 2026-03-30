package com.learninghero.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learninghero.config.AiConfig;
import com.learninghero.common.BusinessException;
import com.learninghero.common.ErrorCode;
import com.learninghero.entity.Question;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

    private final AiConfig aiConfig;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Question> generateQuestions(String topic, String difficulty, int count) {
        String prompt = buildPrompt(topic, difficulty, count);

        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", aiConfig.getModel());
            requestBody.put("messages", List.of(
                    Map.of("role", "user", "content", prompt)
            ));
            requestBody.put("max_tokens", aiConfig.getMaxTokens());
            requestBody.put("temperature", aiConfig.getTemperature());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(Objects.requireNonNull(aiConfig.getApiKey()));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            log.info("调用AI生成题目, topic: {}, difficulty: {}, count: {}", topic, difficulty, count);

            ResponseEntity<String> response = restTemplate.exchange(
                    Objects.requireNonNull(aiConfig.getApiUrl()),
                    Objects.requireNonNull(HttpMethod.POST),
                    entity,
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.error("AI接口调用失败: status={}", response.getStatusCode());
                throw new BusinessException(ErrorCode.AI_API_ERROR, "AI接口调用失败");
            }

            JsonNode jsonResponse = objectMapper.readTree(response.getBody());
            String content = jsonResponse.path("choices").path(0).path("message").path("content").asText();

            return parseQuestions(content, topic, difficulty);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("AI生成题目异常", e);
            throw new BusinessException(ErrorCode.AI_API_ERROR, "AI生成题目失败: " + e.getMessage());
        }
    }

    private String buildPrompt(String topic, String difficulty, int count) {
        String difficultyDesc = switch (difficulty.toLowerCase()) {
            case "easy" -> "简单：基础知识，答案明显，干扰项差异大";
            case "hard" -> "困难：深度知识，需要分析，干扰项高度相似";
            default -> "中等：进阶知识，需要思考，干扰项有迷惑性";
        };

        return String.format("""
                你是一位专业的教育专家，请围绕主题"%s"生成%d道%s难度的选择题。

                难度说明：%s

                要求：
                1. 每道题有4个选项，只有1个正确答案
                2. 问题表述清晰，无歧义
                3. 四个选项长度相近
                4. 干扰项具有合理性
                5. 提供详细的答案解析
                6. 返回JSON格式，不要包含其他内容

                返回格式：
                {
                  "questions": [
                    {
                      "question": "问题内容",
                      "options": ["选项A", "选项B", "选项C", "选项D"],
                      "answer": 0,
                      "explanation": "答案解析"
                    }
                  ]
                }
                """, topic, count, difficultyDesc, difficultyDesc);
    }

    private List<Question> parseQuestions(String content, String topic, String difficulty) {
        try {
            String jsonContent = content;
            if (content.contains("```json")) {
                jsonContent = content.substring(content.indexOf("```json") + 7, content.lastIndexOf("```"));
            } else if (content.contains("```")) {
                jsonContent = content.substring(content.indexOf("```") + 3, content.lastIndexOf("```"));
            }
            jsonContent = jsonContent.trim();

            JsonNode rootNode = objectMapper.readTree(jsonContent);
            JsonNode questionsNode = rootNode.path("questions");

            List<Question> questions = new ArrayList<>();
            for (JsonNode node : questionsNode) {
                Question question = new Question();
                question.setTopic(topic);
                question.setDifficulty(difficulty);
                question.setQuestion(node.path("question").asText());
                question.setExplanation(node.path("explanation").asText());

                JsonNode optionsNode = node.path("options");
                if (optionsNode.isArray() && optionsNode.size() >= 4) {
                    question.setOptionA(optionsNode.get(0).asText());
                    question.setOptionB(optionsNode.get(1).asText());
                    question.setOptionC(optionsNode.get(2).asText());
                    question.setOptionD(optionsNode.get(3).asText());
                }

                question.setAnswer(node.path("answer").asInt());
                questions.add(question);
            }

            if (questions.isEmpty()) {
                throw new BusinessException(ErrorCode.AI_API_ERROR, "AI未生成有效题目");
            }

            return questions;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("解析AI返回内容失败: {}", content, e);
            throw new BusinessException(ErrorCode.AI_API_ERROR, "解析AI返回内容失败");
        }
    }

    @Data
    public static class GeneratedQuestion {
        private String question;
        private List<String> options;
        private Integer answer;
        private String explanation;
    }
}
