package com.learninghero.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EndSessionRequest {

    @NotBlank(message = "会话ID不能为空")
    private String sessionId;
}
