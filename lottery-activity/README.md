# 活动服务 (lottery-activity)

## 功能说明

活动服务负责活动信息管理、活动规则配置和活动参与记录。

## 主要文件

### 控制器

- `ActivityController.java`：活动接口，包含获取活动信息、参与活动等方法

### 服务实现

- `ActivityServiceImpl.java`：活动服务实现

### 实体类

- `Activity.java`：活动实体类

### Mapper 接口

- `ActivityMapper.java`：活动表操作接口

### 配置文件

- `application.yml`：应用配置
- `application-dev.yml`：开发环境配置

## API 接口

- `GET /api/v1/activity/info`：获取活动信息
- `POST /api/v1/activity/join`：参与活动

## 端口

- 8083：HTTP 端口
