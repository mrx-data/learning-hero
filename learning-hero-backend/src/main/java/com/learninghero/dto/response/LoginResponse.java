package com.learninghero.dto.response;

import lombok.Data;

@Data
public class LoginResponse {

    private String token;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private UserInfoResponse userInfo;
}
