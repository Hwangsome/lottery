# 抽奖服务 (lottery-prize) - 核心服务

## 功能说明

抽奖服务是系统的核心服务，负责抽奖逻辑、概率计算、奖品管理和中奖记录。

## 主要文件

### 控制器

- `LotteryController.java`：抽奖接口，包含执行抽奖等方法

### 服务实现

- `LotteryService.java`：抽奖核心服务实现
- `ProbabilityEngine.java`：概率计算引擎
- `StockService.java`：库存管理服务
- `ChanceService.java`：抽奖机会管理服务

### 实体类

- `Prize.java`：奖品实体类
- `WinningRecord.java`：中奖记录实体类

### Mapper 接口

- `PrizeMapper.java`：奖品表操作接口
- `WinningRecordMapper.java`：中奖记录表操作接口

### 配置文件

- `application.yml`：应用配置
- `application-dev.yml`：开发环境配置

## API 接口

- `POST /api/v1/lottery/draw`：执行抽奖

## 端口

- 8084：HTTP 端口
