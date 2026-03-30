package com.learninghero.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GenerateQuestionsRequest {

    @NotBlank(message = "学习主题不能为空")
    @Size(min = 2, max = 50, message = "主题长度必须在2-50个字符之间")
    private String topic;

    @NotBlank(message = "难度不能为空")
    private String difficulty;

    @Min(value = 5, message = "题目数量最少5道")
    @Max(value = 20, message = "题目数量最多20道")
    private Integer count = 10;
}
