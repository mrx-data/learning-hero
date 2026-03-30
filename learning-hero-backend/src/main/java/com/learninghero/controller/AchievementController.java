package com.learninghero.controller;

import com.learninghero.common.Result;
import com.learninghero.config.WebMvcConfig;
import com.learninghero.dto.response.AchievementVO;
import com.learninghero.service.AchievementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/achievements")
@RequiredArgsConstructor
@Tag(name = "成就系统", description = "成就列表、用户成就等接口")
public class AchievementController {

    private final AchievementService achievementService;

    @GetMapping
    @Operation(summary = "获取所有成就", description = "获取系统中所有成就列表及用户解锁状态", security = @SecurityRequirement(name = "Bearer Authentication"))
    public Result<List<AchievementVO>> getAllAchievements() {
        Long userId = WebMvcConfig.getCurrentUserId();
        return Result.success(achievementService.getAllAchievements(userId));
    }

    @GetMapping("/mine")
    @Operation(summary = "获取我的成就", description = "获取当前用户已解锁的成就列表", security = @SecurityRequirement(name = "Bearer Authentication"))
    public Result<List<AchievementVO>> getMyAchievements() {
        Long userId = WebMvcConfig.getCurrentUserId();
        return Result.success(achievementService.getUserAchievements(userId));
    }
}
