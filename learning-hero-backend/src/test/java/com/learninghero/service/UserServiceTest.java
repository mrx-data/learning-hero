package com.learninghero.service;

import com.learninghero.config.JwtConfig;
import com.learninghero.dto.request.LoginRequest;
import com.learninghero.dto.request.UpdateUserRequest;
import com.learninghero.dto.response.LoginResponse;
import com.learninghero.dto.response.UserInfoResponse;
import com.learninghero.entity.User;
import com.learninghero.mapper.UserMapper;
import com.learninghero.util.JwtUtil;
import com.learninghero.util.WechatUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private WechatUtil wechatUtil;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private JwtConfig jwtConfig;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setOpenid("test_openid");
        testUser.setNickName("测试用户");
        testUser.setAvatarUrl("https://example.com/avatar.png");
        testUser.setTotalQuestions(0);
        testUser.setCorrectCount(0);
        testUser.setStudyDays(0);
    }

    @Test
    void testLogin_NewUser() {
        LoginRequest request = new LoginRequest();
        request.setCode("test_code");
        request.setNickName("新用户");
        request.setAvatarUrl("https://example.com/new_avatar.png");

        when(wechatUtil.getOpenid("test_code")).thenReturn("new_openid");
        when(userMapper.selectOne(any())).thenReturn(null);
        when(userMapper.insert(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(2L);
            return 1;
        });
        when(jwtUtil.generateToken(anyLong(), anyString())).thenReturn("test_token");
        when(jwtConfig.getExpiration()).thenReturn(86400000L);

        LoginResponse response = userService.login(request);

        assertNotNull(response);
        assertEquals("test_token", response.getToken());
        assertEquals("Bearer", response.getTokenType());
        assertNotNull(response.getUserInfo());
    }

    @Test
    void testLogin_ExistingUser() {
        LoginRequest request = new LoginRequest();
        request.setCode("test_code");
        request.setNickName("更新昵称");

        when(wechatUtil.getOpenid("test_code")).thenReturn("test_openid");
        when(userMapper.selectOne(any())).thenReturn(testUser);
        when(userMapper.updateById(any(User.class))).thenReturn(1);
        when(jwtUtil.generateToken(anyLong(), anyString())).thenReturn("test_token");
        when(jwtConfig.getExpiration()).thenReturn(86400000L);

        LoginResponse response = userService.login(request);

        assertNotNull(response);
        assertEquals("test_token", response.getToken());
    }

    @Test
    void testGetUserInfo() {
        when(userMapper.selectById(1L)).thenReturn(testUser);

        UserInfoResponse response = userService.getUserInfo(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("测试用户", response.getNickName());
        assertEquals(0, response.getTotalQuestions());
    }

    @Test
    void testUpdateStudyStats() {
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        userService.updateStudyStats(1L, 10, 8);

        assertEquals(10, testUser.getTotalQuestions());
        assertEquals(8, testUser.getCorrectCount());
        verify(userMapper, times(1)).updateById(testUser);
    }

    @Test
    void testUpdateUserInfo() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setNickName("新昵称");
        request.setAvatarUrl("https://example.com/new_avatar.png");

        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        UserInfoResponse response = userService.updateUserInfo(1L, request);

        assertNotNull(response);
        assertEquals("新昵称", response.getNickName());
    }
}
