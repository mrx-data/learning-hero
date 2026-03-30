package com.learninghero.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("achievements")
public class Achievement {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String code;

    private String name;

    private String description;

    @TableField("icon_url")
    private String iconUrl;

    @TableField("condition_type")
    private String conditionType;

    @TableField("condition_value")
    private Integer conditionValue;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
