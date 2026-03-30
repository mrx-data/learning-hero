package com.learninghero.controller;

import com.learninghero.common.Result;
import com.learninghero.config.WebMvcConfig;
import com.learninghero.dto.response.PageResponse;
import com.learninghero.dto.response.StudyRecordVO;
import com.learninghero.dto.response.WrongAnswerVO;
import com.learninghero.service.RecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
@Tag(name = "学习记录", description = "学习历史记录、错题本等接口")
public class RecordController {

    private final RecordService recordService;

    @GetMapping("/history")
    @Operation(summary = "获取学习历史", description = "分页获取当前用户的学习历史记录", security = @SecurityRequirement(name = "Bearer Authentication"))
    public Result<PageResponse<StudyRecordVO>> getHistory(
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Long userId = WebMvcConfig.getCurrentUserId();
        return Result.success(recordService.getStudyHistory(userId, page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取学习记录详情", description = "获取指定学习记录的详细信息", security = @SecurityRequirement(name = "Bearer Authentication"))
    public Result<StudyRecordVO> getDetail(@Parameter(description = "学习记录ID") @PathVariable Long id) {
        Long userId = WebMvcConfig.getCurrentUserId();
        return Result.success(recordService.getStudyRecordDetail(userId, id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除学习记录", description = "删除指定的学习记录", security = @SecurityRequirement(name = "Bearer Authentication"))
    public Result<Void> deleteRecord(@Parameter(description = "学习记录ID") @PathVariable Long id) {
        Long userId = WebMvcConfig.getCurrentUserId();
        recordService.deleteStudyRecord(userId, id);
        return Result.success();
    }

    @GetMapping("/wrong")
    @Operation(summary = "获取错题列表", description = "分页获取当前用户的错题列表", security = @SecurityRequirement(name = "Bearer Authentication"))
    public Result<PageResponse<WrongAnswerVO>> getWrongAnswers(
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Long userId = WebMvcConfig.getCurrentUserId();
        return Result.success(recordService.getWrongAnswers(userId, page, size));
    }

    @DeleteMapping("/wrong/{id}")
    @Operation(summary = "移除错题", description = "从错题本中移除指定题目", security = @SecurityRequirement(name = "Bearer Authentication"))
    public Result<Void> removeWrongAnswer(@Parameter(description = "错题ID") @PathVariable Long id) {
        Long userId = WebMvcConfig.getCurrentUserId();
        recordService.removeWrongAnswer(userId, id);
        return Result.success();
    }
}
