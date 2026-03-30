package com.learninghero.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmitAnswerRequest {

    @NotBlank(message = "会话ID不能为空")
    private String sessionId;

    @NotNull(message = "题目ID不能为空")
    private Long questionId;

    @NotNull(message = "答案不能为空")
    @Min(value = 0, message = "答案值必须在0-3之间")
    private Integer answer;

    @Min(value = 1, message = "答题时间必须大于0")
    private Integer answerTime;
}
