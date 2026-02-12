# Docker 快捷脚本

用于一键启动、停止、查看当前 `docker-compose.yml` 的服务状态。

## 脚本列表

- `start.sh`：启动全部服务（默认 `--build`）
- `stop.sh`：停止全部服务
- `status.sh`：查看服务状态
- `docker-service.sh`：保留的完整管理脚本（支持 logs/restart/build 等）

## 用法

在项目根目录执行：

```bash
mvn -DskipTests clean package
./scripts/start.sh
./scripts/stop.sh
./scripts/status.sh
```

可选参数：

```bash
./scripts/start.sh --no-build   # 启动但不重新构建镜像
./scripts/stop.sh --volumes     # 停止并删除数据卷
```

## 说明

- 脚本会自动兼容 `docker-compose` 和 `docker compose` 两种命令。
- `start.sh` 会检查 Docker 是否已启动。
