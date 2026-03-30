package com.learninghero.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class AnswerDetailVO {

    private Long questionId;
    private String question;
    private List<String> options;
    private Integer correctAnswer;
    private Integer userAnswer;
    private Boolean isCorrect;
    private String explanation;
}
