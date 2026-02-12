# 牙科诊所大转盘抽奖系统 - 后端架构

## 项目简介

牙科诊所大转盘抽奖系统是一个基于 Spring Boot 3.2 + Spring Cloud Alibaba + Dubbo 3.x 的微服务架构项目，为小程序端提供完整的抽奖、积分、用户管理等功能。

## 技术架构

### 核心技术栈

- **Spring Boot 3.2**：基础框架
- **Spring Cloud Alibaba 2023.0.1.1**：服务注册/发现、配置管理
- **Dubbo 3.x**：高性能 RPC 框架
- **Nacos 2.3.2**：服务注册/发现中心
- **MyBatis Plus 3.5.6**：简化数据库操作
- **MySQL 8.0**：关系型数据库
- **Redis 7.2**：缓存和限流
- **Kafka 3.5**：异步消息处理
- **Docker Compose**：容器化部署

### 项目结构

```
lottery-backend/
├── lottery-common/          # 公共模块
├── lottery-api/            # Dubbo 接口定义
├── lottery-gateway/        # API 网关（8080）
├── lottery-auth/           # 认证服务（8081）
├── lottery-user/           # 用户服务（8082）
├── lottery-activity/       # 活动服务（8083）
├── lottery-prize/          # 抽奖服务（8084）
├── lottery-points/         # 积分服务（8085）
├── lottery-notify/         # 通知服务（8086）
├── lottery-admin/          # 管理后台服务（8087）
├── sql/                    # 数据库脚本
└── docker-compose.yml      # Docker Compose 配置
```

## 服务说明

### 1. 公共模块 (lottery-common)

提供项目通用的常量、工具类、异常处理、统一响应格式等。

### 2. API 接口模块 (lottery-api)

定义 Dubbo RPC 接口和数据传输对象 (DTO)，供各服务间调用。

### 3. API 网关 (lottery-gateway)

- 请求路由和转发
- 统一认证和授权
- 请求限流
- API 文档 (Swagger 3.0)

### 4. 认证服务 (lottery-auth)

- 用户登录和注册
- JWT Token 生成和验证
- 微信小程序授权

### 5. 用户服务 (lottery-user)

- 用户信息管理
- 用户统计数据
- 积分管理

### 6. 活动服务 (lottery-activity)

- 活动信息管理
- 活动规则配置
- 活动参与记录

### 7. 抽奖服务 (lottery-prize) - 核心服务

- 抽奖核心逻辑
- 概率计算引擎
- 奖品管理
- 库存管理
- 中奖记录

### 8. 积分服务 (lottery-points)

- 积分余额管理
- 积分变动记录
- 每日签到
- 积分兑换抽奖机会

### 9. 通知服务 (lottery-notify)

- 中奖通知
- 积分变动通知
- 活动通知
- 微信红包发放

### 10. 管理后台服务 (lottery-admin)

- 系统管理
- 数据统计
- 后台用户管理

## 快速开始

### 1. 环境准备

- Docker 19.03+
- Docker Compose 1.25+
- JDK 17+
- Maven 3.6+

### 2. 本地编译 JAR（必须）

```bash
cd /Users/bill/code/clnic_lottery/lottery-backend
mvn -DskipTests clean package
```

### 3. Docker Compose 一键启动全部服务

```bash
cd /Users/bill/code/clnic_lottery/lottery-backend
docker-compose up -d --build
```

### 4. 查看服务状态与日志

```bash
docker-compose ps
docker-compose logs -f lottery-gateway
```

### 5. 访问 API 文档

启动网关服务后，访问：http://localhost:8080/swagger-ui.html

## 数据库初始化

执行 `sql/init.sql` 文件初始化数据库。

## 开发规范

- 代码风格：遵循 Alibaba Java 开发规范
- 接口规范：使用 Dubbo RPC 接口
- 数据库操作：使用 MyBatis Plus
- 日志规范：使用 SLF4J + Logback
- 响应格式：统一使用 Result 类

## 联系方式

如有问题，请联系开发团队。
# lottery
