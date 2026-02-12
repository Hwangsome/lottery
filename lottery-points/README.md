# 积分服务 (lottery-points)

## 功能说明

积分服务负责积分余额管理、积分变动记录、每日签到和积分兑换抽奖机会。

## 主要文件

### 控制器

- `PointsController.java`：积分接口，包含获取积分余额、积分明细、签到等方法

### 服务实现

- `PointsServiceImpl.java`：积分服务实现

### 实体类

- `PointsRecord.java`：积分记录实体类
- `CheckinRecord.java`：签到记录实体类

### Mapper 接口

- `PointsRecordMapper.java`：积分记录表操作接口
- `CheckinRecordMapper.java`：签到记录表操作接口

### 配置文件

- `application.yml`：应用配置
- `application-dev.yml`：开发环境配置

## API 接口

- `GET /api/points/balance`：获取积分余额
- `GET /api/points/records`：获取积分明细
- `POST /api/points/checkin`：每日签到
- `GET /api/points/checkin/status`：检查今日是否已签到
- `GET /api/points/checkin/consecutive`：获取连续签到天数
- `POST /api/points/exchange/chances`：积分兑换抽奖机会
- `GET /api/points/expiring`：获取即将过期的积分

## 端口

- 8085：HTTP 端口
