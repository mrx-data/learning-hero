package com.learninghero.common;

import lombok.Getter;

@Getter
public enum ErrorCode {

    SUCCESS(200, "success"),
    PARAM_ERROR(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "方法不允许"),
    INTERNAL_ERROR(500, "服务器内部错误"),

    USER_NOT_FOUND(1001, "用户不存在"),
    USER_ALREADY_EXISTS(1002, "用户已存在"),
    LOGIN_FAILED(1003, "登录失败"),
    TOKEN_INVALID(1004, "Token无效"),
    TOKEN_EXPIRED(1005, "Token已过期"),

    QUESTION_GENERATE_FAILED(2001, "题目生成失败"),
    QUESTION_NOT_FOUND(2002, "题目不存在"),
    SESSION_NOT_FOUND(2003, "答题会话不存在"),
    SESSION_EXPIRED(2004, "答题会话已过期"),

    RECORD_NOT_FOUND(3001, "学习记录不存在"),

    WECHAT_API_ERROR(4001, "微信接口调用失败"),
    AI_API_ERROR(4002, "AI接口调用失败");

    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
