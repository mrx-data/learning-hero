package com.learninghero.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class StudyResultResponse {

    private Long recordId;
    private Integer score;
    private Integer totalQuestions;
    private Integer correctCount;
    private Double accuracy;
    private Integer duration;
    private List<AnswerDetailVO> answers;
}
