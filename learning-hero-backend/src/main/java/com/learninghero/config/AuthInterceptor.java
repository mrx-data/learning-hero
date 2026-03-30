package com.learninghero.config;

import com.learninghero.common.BusinessException;
import com.learninghero.common.ErrorCode;
import com.learninghero.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "请先登录");
        }

        String token = authorization.substring(7);
        if (!jwtUtil.validateToken(token)) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID, "Token无效或已过期");
        }

        Long userId = jwtUtil.getUserId(token);
        request.setAttribute("userId", userId);
        request.setAttribute("openid", jwtUtil.getOpenid(token));

        return true;
    }
}
