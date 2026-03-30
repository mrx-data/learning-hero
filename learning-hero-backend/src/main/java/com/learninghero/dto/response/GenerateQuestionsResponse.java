package com.learninghero.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class GenerateQuestionsResponse {

    private String sessionId;
    private List<QuestionVO> questions;
    private Integer totalCount;
}
