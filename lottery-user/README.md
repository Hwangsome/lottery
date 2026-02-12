# 用户服务 (lottery-user)

## 功能说明

用户服务负责用户信息管理、用户统计数据和积分管理。

## 主要文件

### 控制器

- `UserController.java`：用户接口，包含获取用户信息、绑定手机号等方法

### 服务实现

- `UserServiceImpl.java`：用户服务实现

### 实体类

- `User.java`：用户实体类

### Mapper 接口

- `UserMapper.java`：用户表操作接口

### 配置文件

- `application.yml`：应用配置
- `application-dev.yml`：开发环境配置

## API 接口

- `GET /api/v1/user/info`：获取用户信息
- `POST /api/v1/user/bindPhone`：绑定手机号

## 端口

- 8082：HTTP 端口
