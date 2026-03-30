package com.learninghero.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("answer_details")
public class AnswerDetail {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("record_id")
    private Long recordId;

    @TableField("question_id")
    private Long questionId;

    @TableField("user_answer")
    private Integer userAnswer;

    @TableField("is_correct")
    private Integer isCorrect;

    @TableField("answer_time")
    private Integer answerTime;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
