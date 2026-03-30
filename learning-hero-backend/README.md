# 学习英雄 - AI 问答引导式学习 后端服务

## 项目简介

学习英雄是一款基于 AI 的问答引导式学习微信小程序后端服务，通过游戏化的闯关答题形式，让用户在轻松愉快的氛围中掌握知识。

## 技术栈

- Java 17
- Spring Boot 3.2.0
- MyBatis Plus 3.5.5
- PostgreSQL (Supabase)
- JWT 认证
- DeepSeek AI API

## 项目结构

```
src/main/java/com/learninghero/
├── LearningHeroApplication.java    # 启动类
├── config/                         # 配置类
│   ├── AiConfig.java
│   ├── AuthInterceptor.java
│   ├── BusinessConfig.java
│   ├── JwtConfig.java
│   ├── MybatisPlusConfig.java
│   ├── WebConfig.java
│   ├── WebMvcConfig.java
│   └── WechatConfig.java
├── controller/                     # 控制器层
│   ├── AchievementController.java
│   ├── RecordController.java
│   ├── StudyController.java
│   └── UserController.java
├── dto/                            # 数据传输对象
│   ├── request/
│   └── response/
├── entity/                         # 实体类
├── mapper/                         # Mapper接口
├── service/                        # 服务层
├── common/                         # 公共类
│   ├── BusinessException.java
│   ├── ErrorCode.java
│   ├── GlobalExceptionHandler.java
│   └── Result.java
└── util/                           # 工具类
    ├── JwtUtil.java
    └── WechatUtil.java
```

## 环境要求

- JDK 17+
- Maven 3.6+
- PostgreSQL 数据库

## 配置说明

### 环境变量

| 变量名 | 说明 | 必填 |
|--------|------|------|
| SPRING_PROFILES_ACTIVE | 运行环境 (dev/prod) | 是 |
| SUPABASE_DB_URL | 数据库连接 URL | 是 |
| SUPABASE_DB_USERNAME | 数据库用户名 | 是 |
| SUPABASE_DB_PASSWORD | 数据库密码 | 是 |
| DEEPSEEK_API_KEY | DeepSeek API 密钥 | 是 |
| WECHAT_APP_ID | 微信小程序 AppID | 是 |
| WECHAT_APP_SECRET | 微信小程序密钥 | 是 |
| JWT_SECRET | JWT 密钥 | 是 |

## 快速开始

### 1. 克隆项目

```bash
cd learning-hero-backend
```

### 2. 配置环境变量

创建 `.env` 文件或设置环境变量：

```bash
export SPRING_PROFILES_ACTIVE=dev
export SUPABASE_DB_URL=jdbc:postgresql://localhost:5432/learning_hero
export SUPABASE_DB_USERNAME=postgres
export SUPABASE_DB_PASSWORD=your_password
export DEEPSEEK_API_KEY=your_deepseek_api_key
export WECHAT_APP_ID=your_wechat_app_id
export WECHAT_APP_SECRET=your_wechat_app_secret
export JWT_SECRET=your_jwt_secret_key
```

### 3. 初始化数据库

执行 `src/main/resources/sql/init.sql` 脚本初始化数据库：

```bash
psql -U postgres -d learning_hero -f src/main/resources/sql/init.sql
```

### 4. 编译项目

```bash
mvn clean package -DskipTests
```

### 5. 运行项目

```bash
java -jar target/learning-hero-backend-1.0.0.jar
```

或使用 Maven：

```bash
mvn spring-boot:run
```

## API 接口

### 用户模块

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 微信登录 | POST | /api/user/login | 微信授权登录 |
| 获取用户信息 | GET | /api/user/info | 获取当前用户信息 |
| 更新用户信息 | PUT | /api/user/info | 更新用户信息 |
| 获取学习统计 | GET | /api/user/statistics | 获取学习统计数据 |

### 学习模块

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 生成题目 | POST | /api/study/questions/generate | AI 生成题目 |
| 提交答案 | POST | /api/study/session/answer | 提交单题答案 |
| 结束答题 | POST | /api/study/session/end | 结束答题会话 |
| 获取结果 | GET | /api/study/session/{id}/result | 获取答题结果 |

### 记录模块

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 学习历史 | GET | /api/records/history | 获取学习历史 |
| 学习详情 | GET | /api/records/{id} | 获取学习详情 |
| 删除记录 | DELETE | /api/records/{id} | 删除学习记录 |
| 错题列表 | GET | /api/records/wrong | 获取错题列表 |
| 移除错题 | DELETE | /api/records/wrong/{id} | 移除错题 |

### 成就模块

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 成就列表 | GET | /api/achievements | 获取所有成就 |
| 我的成就 | GET | /api/achievements/mine | 获取用户成就 |

## 运行测试

```bash
mvn test
```

## 构建部署

### 构建 Docker 镜像

```bash
docker build -t learning-hero-backend:latest .
```

### 运行 Docker 容器

```bash
docker run -d \
  --name learning-hero-backend \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SUPABASE_DB_URL=your_db_url \
  -e SUPABASE_DB_USERNAME=your_username \
  -e SUPABASE_DB_PASSWORD=your_password \
  -e DEEPSEEK_API_KEY=your_api_key \
  -e WECHAT_APP_ID=your_app_id \
  -e WECHAT_APP_SECRET=your_app_secret \
  -e JWT_SECRET=your_jwt_secret \
  learning-hero-backend:latest
```

## 许可证

MIT License
