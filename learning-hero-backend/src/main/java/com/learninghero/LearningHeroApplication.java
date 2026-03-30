package com.learninghero;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.learninghero.mapper")
public class LearningHeroApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearningHeroApplication.class, args);
    }
}
