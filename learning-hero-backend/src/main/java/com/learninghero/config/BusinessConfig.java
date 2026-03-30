package com.learninghero.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "business.question")
public class BusinessConfig {

    private Integer defaultCount;
    private Integer maxCount;
    private Integer minCount;
    private Integer timeLimit;

    private DifficultyScore difficulty;

    @Data
    public static class DifficultyScore {
        private Integer easyScore;
        private Integer mediumScore;
        private Integer hardScore;
    }

    public Integer getScoreByDifficulty(String difficulty) {
        if (difficulty == null) {
            return this.difficulty.getMediumScore();
        }
        return switch (difficulty.toLowerCase()) {
            case "easy" -> this.difficulty.getEasyScore();
            case "hard" -> this.difficulty.getHardScore();
            default -> this.difficulty.getMediumScore();
        };
    }
}
