package com.learninghero.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {

    @Size(max = 64, message = "昵称长度不能超过64个字符")
    private String nickName;

    @Size(max = 512, message = "头像URL长度不能超过512个字符")
    private String avatarUrl;
}
