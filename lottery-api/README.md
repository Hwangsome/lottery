# API 接口模块 (lottery-api)

## 功能说明

API 接口模块定义了 Dubbo RPC 接口和数据传输对象 (DTO)，供各服务间调用。

## 主要文件

### 服务接口

- `UserService.java`：用户服务接口
- `PointsService.java`：积分服务接口
- `ActivityService.java`：活动服务接口
- `PrizeService.java`：奖品服务接口
- `WinningRecordService.java`：中奖记录服务接口
- `ChanceService.java`：抽奖机会服务接口
- `StockService.java`：库存服务接口

### 数据传输对象 (DTO)

- `UserDTO.java`：用户信息 DTO
- `PointsRecordDTO.java`：积分记录 DTO
- `ActivityDTO.java`：活动信息 DTO
- `PrizeDTO.java`：奖品信息 DTO
- `WinningRecordDTO.java`：中奖记录 DTO
- `ChanceRecordDTO.java`：抽奖机会记录 DTO
- `StockDTO.java`：库存信息 DTO
- `LotteryResultDTO.java`：抽奖结果 DTO

## 使用说明

该模块被其他服务引用，通过 Dubbo RPC 调用接口方法。
