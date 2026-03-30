package com.learninghero.controller;

import com.learninghero.common.Result;
import com.learninghero.config.WebMvcConfig;
import com.learninghero.dto.request.EndSessionRequest;
import com.learninghero.dto.request.GenerateQuestionsRequest;
import com.learninghero.dto.request.SubmitAnswerRequest;
import com.learninghero.dto.response.GenerateQuestionsResponse;
import com.learninghero.dto.response.StudyResultResponse;
import com.learninghero.dto.response.SubmitAnswerResponse;
import com.learninghero.service.StudyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/study")
@RequiredArgsConstructor
@Tag(name = "学习管理", description = "题目生成、答题、学习会话管理等接口")
public class StudyController {

    private final StudyService studyService;

    @PostMapping("/questions/generate")
    @Operation(summary = "生成题目", description = "使用AI生成指定主题和难度的题目", security = @SecurityRequirement(name = "Bearer Authentication"))
    public Result<GenerateQuestionsResponse> generateQuestions(@Valid @RequestBody GenerateQuestionsRequest request) {
        Long userId = WebMvcConfig.getCurrentUserId();
        return Result.success(studyService.generateQuestions(userId, request));
    }

    @PostMapping("/session/answer")
    @Operation(summary = "提交答案", description = "提交答题结果并获取正确答案", security = @SecurityRequirement(name = "Bearer Authentication"))
    public Result<SubmitAnswerResponse> submitAnswer(@Valid @RequestBody SubmitAnswerRequest request) {
        Long userId = WebMvcConfig.getCurrentUserId();
        return Result.success(studyService.submitAnswer(userId, request));
    }

    @PostMapping("/session/end")
    @Operation(summary = "结束学习会话", description = "结束当前学习会话并生成学习报告", security = @SecurityRequirement(name = "Bearer Authentication"))
    public Result<StudyResultResponse> endSession(@Valid @RequestBody EndSessionRequest request) {
        Long userId = WebMvcConfig.getCurrentUserId();
        return Result.success(studyService.endSession(userId, request));
    }

    @GetMapping("/session/{id}/result")
    @Operation(summary = "获取学习结果", description = "获取指定学习会话的详细结果", security = @SecurityRequirement(name = "Bearer Authentication"))
    public Result<StudyResultResponse> getResult(@Parameter(description = "学习会话ID") @PathVariable Long id) {
        Long userId = WebMvcConfig.getCurrentUserId();
        return Result.success(studyService.getResult(userId, id));
    }
}
