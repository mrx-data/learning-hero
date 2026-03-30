package com.learninghero.dto.response;

import lombok.Data;

@Data
public class UserInfoResponse {

    private Long id;
    private String nickName;
    private String avatarUrl;
    private Integer totalQuestions;
    private Integer correctCount;
    private Integer studyDays;
    private Double accuracy;
}
