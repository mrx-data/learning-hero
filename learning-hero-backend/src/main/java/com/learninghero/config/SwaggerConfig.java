package com.learninghero.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI learningHeroOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(
                        new Server().url("http://localhost:" + serverPort).description("开发服务器")
                ))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("请输入JWT认证令牌")
                        )
                );
    }

    private Info apiInfo() {
        return new Info()
                .title("学习英雄 API")
                .description("AI问答引导式学习平台后端服务API文档\n\n" +
                        "## 功能模块\n" +
                        "- **用户管理**：用户登录、注册、信息管理\n" +
                        "- **题目管理**：AI生成题目、题目查询\n" +
                        "- **学习记录**：学习会话管理、答题记录\n" +
                        "- **错题本**：错题收集、错题复习\n" +
                        "- **成就系统**：成就解锁、成就展示\n\n" +
                        "## 认证说明\n" +
                        "大部分接口需要JWT认证，请在用户登录后获取token，并在请求头中添加：\n" +
                        "```\n" +
                        "Authorization: Bearer <your_token>\n" +
                        "```")
                .version("1.0.0")
                .contact(new Contact()
                        .name("学习英雄团队")
                        .email("support@learninghero.com"))
                .license(new License()
                        .name("Apache 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0"));
    }
}
