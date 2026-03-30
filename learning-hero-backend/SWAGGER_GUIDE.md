# Swagger API 文档使用说明

## 概述

本项目已集成 Swagger (springdoc-openapi) 接口文档工具，提供自动化的 API 文档生成、接口测试和交互功能。

## 访问地址

启动应用后，可以通过以下地址访问 Swagger 文档：

- **Swagger UI 界面**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON 文档**: http://localhost:8080/v3/api-docs
- **OpenAPI YAML 文档**: http://localhost:8080/v3/api-docs.yaml

## 功能特性

### 1. 自动化文档生成

- ✅ 自动扫描 `com.learninghero.controller` 包下的所有 Controller
- ✅ 自动识别 Spring MVC 注解（@RestController, @RequestMapping 等）
- ✅ 自动解析请求参数和响应模型
- ✅ 支持参数验证注解的展示

### 2. 接口测试功能

- ✅ 在线调试 API 接口
- ✅ 支持请求参数填写和验证
- ✅ 实时查看响应结果
- ✅ 支持文件上传测试

### 3. JWT 认证支持

- ✅ 集成 Bearer Token 认证
- ✅ 支持 JWT Token 输入和验证
- ✅ 自动在请求头添加 Authorization

## 使用指南

### 1. 访问 Swagger UI

1. 启动应用
2. 打开浏览器访问：http://localhost:8080/swagger-ui.html
3. 查看 API 文档列表

### 2. 测试需要认证的接口

#### 步骤 1: 获取 JWT Token

1. 找到 **用户管理** 分组
2. 点击 **POST /api/user/login** 接口
3. 点击 **Try it out** 按钮
4. 输入登录参数（微信小程序 code）
5. 点击 **Execute** 执行请求
6. 从响应中复制 `token` 值

#### 步骤 2: 配置认证

1. 点击页面右上角的 **Authorize** 按钮
2. 在弹出框中输入：`Bearer <your_token>`
   - 例如：`Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...`
3. 点击 **Authorize** 确认
4. 点击 **Close** 关闭对话框

#### 步骤 3: 测试接口

现在可以测试所有需要认证的接口了：
- 系统会自动在请求头中添加 `Authorization: Bearer <your_token>`
- 无需每次手动输入 token

### 3. 测试接口示例

#### 示例 1: 生成题目

1. 找到 **学习管理** 分组
2. 点击 **POST /api/study/questions/generate**
3. 点击 **Try it out**
4. 输入请求体：
   ```json
   {
     "topic": "数学",
     "difficulty": "medium",
     "count": 5
   }
   ```
5. 点击 **Execute**
6. 查看响应结果

#### 示例 2: 获取学习历史

1. 找到 **学习记录** 分组
2. 点击 **GET /api/records/history**
3. 点击 **Try it out**
4. 设置分页参数：
   - page: 1
   - size: 10
5. 点击 **Execute**
6. 查看响应结果

## API 分组说明

### 用户管理
- 用户登录
- 获取用户信息
- 更新用户信息
- 获取用户统计信息

### 学习管理
- 生成题目
- 提交答案
- 结束学习会话
- 获取学习结果

### 学习记录
- 获取学习历史
- 获取学习记录详情
- 删除学习记录
- 获取错题列表
- 移除错题

### 成就系统
- 获取所有成就
- 获取我的成就

## 配置说明

### application.yml 配置

```yaml
springdoc:
  api-docs:
    enabled: true                    # 启用 API 文档
    path: /v3/api-docs              # API 文档路径
  swagger-ui:
    enabled: true                    # 启用 Swagger UI
    path: /swagger-ui.html          # Swagger UI 路径
    tags-sorter: alpha              # 按字母顺序排序标签
    operations-sorter: alpha        # 按字母顺序排序操作
    display-request-duration: true  # 显示请求耗时
    deep-linking: true              # 启用深链接
  packages-to-scan: com.learninghero.controller  # 扫描的包
  paths-to-match: /api/**           # 匹配的路径
```

### 访问权限控制

Swagger 相关路径已配置为无需认证即可访问：

```java
.excludePathPatterns(
    "/api/user/login",
    "/swagger-ui/**",
    "/swagger-ui.html",
    "/v3/api-docs/**",
    "/v3/api-docs.yaml",
    "/webjars/**"
)
```

## 开发指南

### 添加新的 API 接口

1. 在 Controller 类上添加 `@Tag` 注解：
   ```java
   @Tag(name = "模块名称", description = "模块描述")
   ```

2. 在方法上添加 `@Operation` 注解：
   ```java
   @Operation(
       summary = "接口名称",
       description = "接口详细描述",
       security = @SecurityRequirement(name = "Bearer Authentication")
   )
   ```

3. 为参数添加 `@Parameter` 注解：
   ```java
   @Parameter(description = "参数说明")
   ```

### 注解说明

| 注解 | 用途 | 示例 |
|------|------|------|
| @Tag | Controller 分组 | `@Tag(name = "用户管理")` |
| @Operation | 接口说明 | `@Operation(summary = "用户登录")` |
| @Parameter | 参数说明 | `@Parameter(description = "用户ID")` |
| @SecurityRequirement | 认证要求 | `@SecurityRequirement(name = "Bearer Authentication")` |

## 常见问题

### Q1: Swagger UI 无法访问？

**解决方案：**
1. 检查应用是否正常启动
2. 确认端口是否正确（默认 8080）
3. 检查是否有防火墙或代理配置

### Q2: 接口返回 401 未授权？

**解决方案：**
1. 确认是否已配置 JWT Token
2. 检查 Token 格式是否正确（需要 Bearer 前缀）
3. 确认 Token 是否过期

### Q3: 如何导出 API 文档？

**解决方案：**
1. 访问 http://localhost:8080/v3/api-docs
2. 右键 → 另存为 JSON 文件
3. 可导入到 Postman、Apifox 等工具

### Q4: 如何在生产环境禁用 Swagger？

**解决方案：**
在 `application-prod.yml` 中配置：
```yaml
springdoc:
  api-docs:
    enabled: false
  swagger-ui:
    enabled: false
```

## 技术栈

- **springdoc-openapi**: 2.3.0
- **OpenAPI Specification**: 3.0
- **Spring Boot**: 3.2.0

## 参考资料

- [springdoc-openapi 官方文档](https://springdoc.org/)
- [OpenAPI Specification](https://swagger.io/specification/)
- [Swagger UI 文档](https://swagger.io/tools/swagger-ui/)

## 更新日志

### v1.0.0 (2026-03-31)
- ✅ 集成 springdoc-openapi
- ✅ 配置 Swagger UI
- ✅ 添加 JWT 认证支持
- ✅ 完善 API 文档注解
- ✅ 配置访问权限控制
