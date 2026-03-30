package com.learninghero.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AchievementVO {

    private Long id;
    private String code;
    private String name;
    private String description;
    private String iconUrl;
    private Boolean achieved;
    private LocalDateTime achievedAt;
}
