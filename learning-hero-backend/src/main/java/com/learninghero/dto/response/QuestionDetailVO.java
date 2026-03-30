package com.learninghero.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class QuestionDetailVO {

    private Long id;
    private String topic;
    private String question;
    private List<String> options;
    private Integer answer;
    private String explanation;
}
