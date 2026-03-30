package com.learninghero.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.learninghero.dto.response.AchievementVO;
import com.learninghero.entity.Achievement;
import com.learninghero.entity.User;
import com.learninghero.entity.UserAchievement;
import com.learninghero.mapper.AchievementMapper;
import com.learninghero.mapper.UserAchievementMapper;
import com.learninghero.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AchievementService {

    private final AchievementMapper achievementMapper;
    private final UserAchievementMapper userAchievementMapper;
    private final UserMapper userMapper;

    public List<AchievementVO> getAllAchievements(Long userId) {
        List<Achievement> achievements = achievementMapper.selectList(null);
        Set<Long> achievedIds = userAchievementMapper.selectList(
                new LambdaQueryWrapper<UserAchievement>().eq(UserAchievement::getUserId, userId)
        ).stream().map(UserAchievement::getAchievementId).collect(Collectors.toSet());

        return achievements.stream()
                .map(a -> convertToVO(a, achievedIds.contains(a.getId())))
                .toList();
    }

    public List<AchievementVO> getUserAchievements(Long userId) {
        List<UserAchievement> userAchievements = userAchievementMapper.selectList(
                new LambdaQueryWrapper<UserAchievement>().eq(UserAchievement::getUserId, userId)
        );

        return userAchievements.stream()
                .map(ua -> {
                    Achievement achievement = achievementMapper.selectById(ua.getAchievementId());
                    AchievementVO vo = convertToVO(achievement, true);
                    vo.setAchievedAt(ua.getAchievedAt());
                    return vo;
                })
                .toList();
    }

    @Transactional
    public List<AchievementVO> checkAndAwardAchievements(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return new ArrayList<>();
        }

        List<Achievement> achievements = achievementMapper.selectList(null);
        Set<Long> achievedIds = userAchievementMapper.selectList(
                new LambdaQueryWrapper<UserAchievement>().eq(UserAchievement::getUserId, userId)
        ).stream().map(UserAchievement::getAchievementId).collect(Collectors.toSet());

        List<AchievementVO> newAchievements = new ArrayList<>();

        for (Achievement achievement : achievements) {
            if (achievedIds.contains(achievement.getId())) {
                continue;
            }

            if (checkCondition(user, achievement)) {
                UserAchievement ua = new UserAchievement();
                ua.setUserId(userId);
                ua.setAchievementId(achievement.getId());
                ua.setAchievedAt(LocalDateTime.now());
                userAchievementMapper.insert(ua);

                AchievementVO vo = convertToVO(achievement, true);
                vo.setAchievedAt(ua.getAchievedAt());
                newAchievements.add(vo);
            }
        }

        return newAchievements;
    }

    private boolean checkCondition(User user, Achievement achievement) {
        String conditionType = achievement.getConditionType();
        int conditionValue = achievement.getConditionValue();

        return switch (conditionType) {
            case "STUDY_COUNT" -> {
                int studyCount = user.getTotalQuestions() > 0 ? 1 : 0;
                yield studyCount >= conditionValue;
            }
            case "QUESTION_COUNT" -> user.getTotalQuestions() >= conditionValue;
            case "STREAK_DAYS" -> user.getStudyDays() >= conditionValue;
            case "PERFECT_SCORE" -> conditionValue == 1;
            default -> false;
        };
    }

    private AchievementVO convertToVO(Achievement achievement, boolean achieved) {
        AchievementVO vo = new AchievementVO();
        vo.setId(achievement.getId());
        vo.setCode(achievement.getCode());
        vo.setName(achievement.getName());
        vo.setDescription(achievement.getDescription());
        vo.setIconUrl(achievement.getIconUrl());
        vo.setAchieved(achieved);
        return vo;
    }
}
