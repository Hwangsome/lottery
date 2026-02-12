# 通知服务 (lottery-notify)

## 功能说明

通知服务负责中奖通知、积分变动通知、活动通知和微信红包发放。

## 主要文件

### 消费者

- `RedpackConsumer.java`：红包发放消费者，监听抽奖中奖事件

### 服务实现

- `RedpackService.java`：红包发放服务

### Kafka 配置

- `KafkaConfig.java`：Kafka 配置

### 配置文件

- `application.yml`：应用配置
- `application-dev.yml`：开发环境配置

## 功能说明

- **红包发放**：监听 `lottery-winning` 主题，发送微信红包
- **通知推送**：发送中奖通知、积分变动通知等

## 端口

- 8086：HTTP 端口
