package com.learninghero.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("study_records")
public class StudyRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    private String topic;

    private String difficulty;

    @TableField("total_questions")
    private Integer totalQuestions;

    @TableField("correct_count")
    private Integer correctCount;

    private Integer score;

    private Integer duration;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
