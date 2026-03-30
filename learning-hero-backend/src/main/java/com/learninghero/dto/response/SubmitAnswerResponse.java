package com.learninghero.dto.response;

import lombok.Data;

@Data
public class SubmitAnswerResponse {

    private Boolean isCorrect;
    private Integer correctAnswer;
    private String explanation;
    private Integer currentScore;
    private Integer currentIndex;
    private Integer totalCount;
}
