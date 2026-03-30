package com.learninghero.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.learninghero.common.BusinessException;
import com.learninghero.common.ErrorCode;
import com.learninghero.config.JwtConfig;
import com.learninghero.dto.request.LoginRequest;
import com.learninghero.dto.request.UpdateUserRequest;
import com.learninghero.dto.response.LoginResponse;
import com.learninghero.dto.response.UserInfoResponse;
import com.learninghero.entity.User;
import com.learninghero.mapper.UserMapper;
import com.learninghero.util.JwtUtil;
import com.learninghero.util.WechatUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final WechatUtil wechatUtil;
    private final JwtUtil jwtUtil;
    private final JwtConfig jwtConfig;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        String openid = wechatUtil.getOpenid(request.getCode());

        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getOpenid, openid)
        );

        if (user == null) {
            user = new User();
            user.setOpenid(openid);
            user.setNickName(request.getNickName() != null ? request.getNickName() : "学习者");
            user.setAvatarUrl(request.getAvatarUrl());
            user.setTotalQuestions(0);
            user.setCorrectCount(0);
            user.setStudyDays(0);
            userMapper.insert(user);
        } else {
            if (request.getNickName() != null && !request.getNickName().equals(user.getNickName())) {
                user.setNickName(request.getNickName());
            }
            if (request.getAvatarUrl() != null && !request.getAvatarUrl().equals(user.getAvatarUrl())) {
                user.setAvatarUrl(request.getAvatarUrl());
            }
            userMapper.updateById(user);
        }

        String token = jwtUtil.generateToken(user.getId(), user.getOpenid());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setTokenType("Bearer");
        response.setExpiresIn(jwtConfig.getExpiration() / 1000);
        response.setUserInfo(buildUserInfoResponse(user));

        return response;
    }

    public UserInfoResponse getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return buildUserInfoResponse(user);
    }

    @Transactional
    public UserInfoResponse updateUserInfo(Long userId, UpdateUserRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        if (request.getNickName() != null) {
            user.setNickName(request.getNickName());
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }

        userMapper.updateById(user);
        return buildUserInfoResponse(user);
    }

    @Transactional
    public void updateStudyStats(Long userId, int questionCount, int correctCount) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        user.setTotalQuestions(user.getTotalQuestions() + questionCount);
        user.setCorrectCount(user.getCorrectCount() + correctCount);

        LocalDate today = LocalDate.now();
        if (!today.equals(user.getLastStudyDate())) {
            user.setStudyDays(user.getStudyDays() + 1);
        }
        user.setLastStudyDate(today);

        userMapper.updateById(user);
    }

    private UserInfoResponse buildUserInfoResponse(User user) {
        UserInfoResponse response = new UserInfoResponse();
        response.setId(user.getId());
        response.setNickName(user.getNickName());
        response.setAvatarUrl(user.getAvatarUrl());
        response.setTotalQuestions(user.getTotalQuestions());
        response.setCorrectCount(user.getCorrectCount());
        response.setStudyDays(user.getStudyDays());

        if (user.getTotalQuestions() != null && user.getTotalQuestions() > 0) {
            response.setAccuracy((double) user.getCorrectCount() / user.getTotalQuestions() * 100);
        } else {
            response.setAccuracy(0.0);
        }

        return response;
    }
}
