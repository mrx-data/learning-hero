package com.learninghero.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StudyRecordVO {

    private Long id;
    private String topic;
    private String difficulty;
    private Integer totalQuestions;
    private Integer correctCount;
    private Integer score;
    private Integer duration;
    private LocalDateTime createdAt;
}
