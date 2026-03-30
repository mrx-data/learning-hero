package com.learninghero.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WrongAnswerVO {

    private Long id;
    private QuestionDetailVO question;
    private Integer wrongCount;
    private LocalDateTime lastWrongAt;
}
