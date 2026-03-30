package com.learninghero.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "ai.deepseek")
public class AiConfig {

    private String apiUrl;
    private String apiKey;
    private String model;
    private Integer maxTokens;
    private Double temperature;
    private Integer timeout;
}
