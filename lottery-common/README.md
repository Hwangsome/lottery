# 公共模块 (lottery-common)

## 功能说明

公共模块提供项目通用的常量、工具类、异常处理、统一响应格式等。

## 主要文件

### 常量类

- `ErrorCode.java`：错误码枚举，定义了项目所有的错误码
- `ResultCode.java`：响应状态码枚举
- `PrizeType.java`：奖品类型枚举
- `PointsType.java`：积分类型枚举
- `ChanceType.java`：抽奖机会类型枚举
- `RecordStatus.java`：中奖记录状态枚举

### 工具类

- `JwtUtil.java`：JWT Token 生成和解析工具
- `CommonUtil.java`：通用工具类（如手机号脱敏、验证码生成）
- `RedisKeyUtil.java`：Redis 键名生成工具
- `AESUtil.java`：AES 加密工具

### 统一响应

- `Result.java`：统一响应类，包含响应状态、数据和消息
- `ResultCode.java`：响应状态码枚举

### 异常处理

- `BizException.java`：业务异常类
- `GlobalExceptionHandler.java`：全局异常处理器

### 其他

- `PageResult.java`：分页结果类
- `PageParam.java`：分页参数类
