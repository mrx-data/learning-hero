package com.learninghero.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "微信授权码不能为空")
    private String code;

    private String nickName;

    private String avatarUrl;
}
