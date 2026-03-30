package com.learninghero.controller;

import com.learninghero.common.Result;
import com.learninghero.config.WebMvcConfig;
import com.learninghero.dto.request.LoginRequest;
import com.learninghero.dto.request.UpdateUserRequest;
import com.learninghero.dto.response.LoginResponse;
import com.learninghero.dto.response.UserInfoResponse;
import com.learninghero.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户登录、注册、信息管理等接口")
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "通过微信小程序code登录，首次登录会自动注册")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(userService.login(request));
    }

    @GetMapping("/info")
    @Operation(summary = "获取用户信息", description = "获取当前登录用户的详细信息", security = @SecurityRequirement(name = "Bearer Authentication"))
    public Result<UserInfoResponse> getUserInfo() {
        Long userId = WebMvcConfig.getCurrentUserId();
        return Result.success(userService.getUserInfo(userId));
    }

    @PutMapping("/info")
    @Operation(summary = "更新用户信息", description = "更新当前登录用户的个人信息", security = @SecurityRequirement(name = "Bearer Authentication"))
    public Result<UserInfoResponse> updateUserInfo(@Valid @RequestBody UpdateUserRequest request) {
        Long userId = WebMvcConfig.getCurrentUserId();
        return Result.success(userService.updateUserInfo(userId, request));
    }

    @GetMapping("/statistics")
    @Operation(summary = "获取用户统计信息", description = "获取当前登录用户的学习统计数据", security = @SecurityRequirement(name = "Bearer Authentication"))
    public Result<UserInfoResponse> getStatistics() {
        Long userId = WebMvcConfig.getCurrentUserId();
        return Result.success(userService.getUserInfo(userId));
    }
}
