package com.learninghero.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class QuestionVO {

    private Long id;
    private String question;
    private List<String> options;
    private String difficulty;
}
