package com.learninghero.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResultTest {

    @Test
    void testSuccess() {
        Result<Void> result = Result.success();

        assertEquals(200, result.getCode());
        assertEquals("success", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void testSuccessWithData() {
        String data = "test data";

        Result<String> result = Result.success(data);

        assertEquals(200, result.getCode());
        assertEquals("success", result.getMessage());
        assertEquals(data, result.getData());
    }

    @Test
    void testError() {
        Result<Void> result = Result.error(400, "参数错误");

        assertEquals(400, result.getCode());
        assertEquals("参数错误", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void testErrorWithErrorCode() {
        Result<Void> result = Result.error(ErrorCode.PARAM_ERROR);

        assertEquals(400, result.getCode());
        assertEquals("请求参数错误", result.getMessage());
    }
}
