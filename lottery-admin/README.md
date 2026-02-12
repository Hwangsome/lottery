# 管理后台服务 (lottery-admin)

## 功能说明

管理后台服务负责系统管理、数据统计和后台用户管理。

## 主要文件

### 控制器

- `AdminController.java`：管理后台接口

### 服务实现

- `AdminServiceImpl.java`：管理后台服务实现

### 配置文件

- `application.yml`：应用配置
- `application-dev.yml`：开发环境配置

## API 接口

- `GET /api/admin/stats`：获取系统统计数据
- `GET /api/admin/users`：获取用户列表
- `GET /api/admin/activities`：获取活动列表
- `GET /api/admin/prizes`：获取奖品列表
- `GET /api/admin/records`：获取中奖记录

## 端口

- 8087：HTTP 端口
