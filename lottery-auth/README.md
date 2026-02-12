# 认证服务 (lottery-auth)

## 功能说明

认证服务负责**微信小程序用户登录**、**管理后台登录**以及 JWT Token 的生成。前端为微信小程序时，用户通过「微信 code 换 openid → 查/建用户 → 发 JWT」完成登录。

---

## 微信小程序登录

- **接口**：`POST /api/v1/user/login`（由网关路由到本服务，路径放行无需 token）
- **请求体**：`{ "code": "微信 wx.login() 返回的 code" }`
- **流程**：
  1. 使用 `code` 调用微信 `jscode2session` 换取 `openid`（及 `session_key`）
  2. 按 `openid` 查询用户：存在则直接使用，不存在则创建新用户（默认昵称「微信用户」，送 3 次抽奖机会）
  3. 生成 JWT（type=user），返回 `token`、`expiresIn`、`userInfo`、`isNewUser`
- **配置**：需在 `application.yml` 中配置 `wechat.appid`、`wechat.secret`（小程序 AppID 与 AppSecret）
- **实现**：`WechatAuthController`、`WechatAuthService`（code2Session、用户查/建、JWT 生成）

---

## 管理后台登录

- **接口**：`POST /api/v1/admin/login`
- **说明**：管理员账号密码登录，返回 admin 类型 JWT，用于后台与核销接口鉴权。见 `AdminAuthController`、`AdminAuthService`。

---

## 主要文件

### 控制器

- `WechatAuthController`：微信小程序登录（`POST /api/v1/user/login`）
- `AdminAuthController`：管理后台登录（`POST /api/v1/admin/login`）

### 服务

- `WechatAuthService`：微信 code2Session、用户查/建、用户端 JWT 生成
- `AdminAuthService`：管理员校验、管理端 JWT 生成

### 配置与依赖

- JWT 使用 `lottery-common` 的 `JwtUtil`；用户数据通过 Dubbo 调用 `lottery-user` 的 `UserService`。
- `application.yml`：`wechat.appid`、`wechat.secret`、`jwt.*` 等。

## 端口

- 以实际 `application.yml` 为准（如 8081）
