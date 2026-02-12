# API 网关 (lottery-gateway)

## 功能说明

API 网关是系统的入口，负责请求路由、统一认证、授权和限流。**当前前端为微信小程序**，用户通过微信小程序登录流程获取 JWT，后续请求在 Header 中携带 Token 访问需登录接口。

---

## 微信小程序登录流程

整体流程：**小程序 wx.login → 后端用 code 换 openid → 查/建用户 → 发 JWT → 后续请求带 Token**。

| 步骤 | 角色 | 说明 |
|------|------|------|
| 1 | 小程序 | 调用 `wx.login()` 获取临时 `code`（5 分钟有效） |
| 2 | 小程序 | 请求 `POST /api/v1/user/login`，Body：`{ "code": "上述 code" }`（无需 token，网关放行） |
| 3 | 网关 | 将请求转发到认证服务 `lottery-auth`，不做鉴权 |
| 4 | lottery-auth | 用 `code` 调微信 `jscode2session` 换取 `openid`（及 `session_key`） |
| 5 | lottery-auth | 按 `openid` 查用户：存在则复用，不存在则创建新用户（新用户送 3 次抽奖机会） |
| 6 | lottery-auth | 生成 JWT（含 userId、openid、type=user），返回 `token`、`expiresIn`、`userInfo`、`isNewUser` |
| 7 | 小程序 | 保存 `token`，后续请求在 Header 中加 `Authorization: Bearer <token>` |
| 8 | 网关 | 对需登录接口校验 JWT，通过后向下游注入 `X-User-Id`，下游据此识别当前用户 |

**登录接口**：`POST /api/v1/user/login`，Body 示例：`{ "code": "0x0xxx" }`。  
**后端配置**：认证服务需配置 `wechat.appid`、`wechat.secret`（小程序 AppID 与 AppSecret），见 `lottery-auth` 的 `application.yml`。

---

## 用户鉴权在哪里

鉴权**只在网关**完成，下游服务不再校验 JWT。

- **实现位置**：`config/GatewaySecurityConfig.java` + `security/` 包
- **流程**：
  1. 请求带 `Authorization: Bearer <JWT>`
  2. `JwtServerAuthenticationConverter` 从请求头取出 token
  3. `JwtReactiveAuthenticationManager` 用 `JwtUtil` 校验 token，并按 token 内 `type` 构造用户/管理员身份（`GatewayAuthPrincipal`）
  4. 路径权限：未认证返回 401，角色不符返回 403

- **路径与权限**：

| 路径 | 权限 | 说明 |
|------|------|------|
| `/api/v1/user/login`、`/api/v1/admin/login`、`/api/v1/lottery/activity`、`/actuator/**` | 放行 | 登录与公开接口 |
| `/api/v1/user/**`、`/api/v1/lottery/**`、`/api/v1/points/**` | 需 **USER** token | 用户端接口 |
| `/api/v1/verify/**`、`/api/v1/admin/**` | 需 **ADMIN** token | 管理端/核销接口 |

- **鉴权通过后**：`AddAuthHeaderWebFilter` 从 SecurityContext 取出当前用户/管理员，**向下游请求注入请求头**（见下节）。

---

## 下游服务怎么拿到用户 ID

网关鉴权通过后，会**自动在转发的请求上加上请求头**，下游只需读请求头即可，无需再解析 JWT。

- **用户端接口**（USER token）：网关注入请求头 **`X-User-Id`**（当前用户 ID，Long）。
- **管理端接口**（ADMIN token）：网关注入请求头 **`X-Admin-Id`**（当前管理员 ID，Long）。

**下游用法示例**（当前各服务做法）：

- 在 Controller 方法参数里用 `@RequestHeader` 读取：
  - 用户 ID：`@RequestHeader("X-User-Id") Long userId`
  - 管理员 ID：`@RequestHeader("X-Admin-Id") Long adminId`
- 若该接口必须登录，不传或无效 token 时请求不会到达下游（网关已 401/403）；可选登录场景可把 `X-User-Id` 设为 `required = false`。

涉及的服务示例：`lottery-user`、`lottery-points`、`lottery-prize`、`lottery-activity` 等均通过 `X-User-Id` 获取当前用户 ID；管理/核销相关通过 `X-Admin-Id` 获取管理员 ID。

---

## 主要文件

### 配置文件

- `application.yml`：网关配置
- `application-dev.yml`：开发环境配置

### 过滤器与鉴权

- **Spring Security**：统一 JWT 鉴权（见 `config/GatewaySecurityConfig.java`、`security/` 包）
- `config/CorsConfig.java`：跨域配置
- `filter/RateLimitFilter.java`：抽奖限流
- `filter/RequestLog.java`：请求日志

### 路由配置

- `GatewayConfig.java`：网关路由配置

### 限流配置

- `RateLimiterConfig.java`：限流配置

## API 文档

启动网关后，访问：http://localhost:8080/swagger-ui.html

## 端口

- 8080：HTTP 端口
