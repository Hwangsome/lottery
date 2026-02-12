#!/bin/bash

# 项目根目录（脚本所在目录的上级目录）
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
DOCKER_COMPOSE_FILE="${PROJECT_ROOT}/docker-compose.yml"

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 检查 docker 和 docker-compose 是否可用
check_dependencies() {
    if ! command -v docker &> /dev/null; then
        echo -e "${RED}错误: 未找到 docker 命令，请确保已安装 Docker${NC}"
        exit 1
    fi

    if ! command -v docker-compose &> /dev/null; then
        echo -e "${RED}错误: 未找到 docker-compose 命令，请确保已安装 Docker Compose${NC}"
        exit 1
    fi

    # 检查 Docker 服务是否正在运行
    if ! docker info &> /dev/null; then
        echo -e "${RED}错误: Docker 服务未运行，请先启动 Docker${NC}"
        exit 1
    fi
}

# 启动所有服务
start_services() {
    echo -e "${GREEN}正在启动所有 Docker 服务...${NC}"
    cd "${PROJECT_ROOT}" || exit 1
    docker-compose -f "${DOCKER_COMPOSE_FILE}" up -d
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}所有服务已成功启动${NC}"
        show_running_services
    else
        echo -e "${RED}启动服务失败${NC}"
        exit 1
    fi
}

# 只启动中间件服务
start_middleware() {
    echo -e "${GREEN}正在启动 Docker 中间件服务...${NC}"
    cd "${PROJECT_ROOT}" || exit 1
    docker-compose -f "${DOCKER_COMPOSE_FILE}" up -d mysql redis nacos zookeeper kafka
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}中间件服务已成功启动${NC}"
        show_running_services
    else
        echo -e "${RED}启动中间件服务失败${NC}"
        exit 1
    fi
}

# 只启动微服务
start_microservices() {
    echo -e "${GREEN}正在启动 Lottery 微服务...${NC}"
    cd "${PROJECT_ROOT}" || exit 1
    docker-compose -f "${DOCKER_COMPOSE_FILE}" up -d lottery-gateway lottery-auth lottery-user lottery-activity lottery-prize lottery-points lottery-notify lottery-admin
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}微服务已成功启动${NC}"
        show_running_services
    else
        echo -e "${RED}启动微服务失败${NC}"
        exit 1
    fi
}

# 停止服务
stop_services() {
    echo -e "${YELLOW}正在停止所有 Docker 服务...${NC}"
    cd "${PROJECT_ROOT}" || exit 1
    docker-compose -f "${DOCKER_COMPOSE_FILE}" down
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}所有服务已成功停止${NC}"
    else
        echo -e "${RED}停止服务失败${NC}"
        exit 1
    fi
}

# 重启服务
restart_services() {
    stop_services
    start_services
}

# 查看服务状态
show_services_status() {
    echo -e "${GREEN}服务状态:${NC}"
    cd "${PROJECT_ROOT}" || exit 1
    docker-compose -f "${DOCKER_COMPOSE_FILE}" ps
}

# 查看运行中的服务
show_running_services() {
    echo -e "${GREEN}运行中的服务:${NC}"
    cd "${PROJECT_ROOT}" || exit 1
    docker-compose -f "${DOCKER_COMPOSE_FILE}" ps --services --filter "status=running"
}

# 查看服务日志
show_services_logs() {
    echo -e "${GREEN}查看服务日志 (按 Ctrl+C 停止):${NC}"
    cd "${PROJECT_ROOT}" || exit 1
    docker-compose -f "${DOCKER_COMPOSE_FILE}" logs -f
}

# 构建服务（重新构建镜像）
build_services() {
    echo -e "${GREEN}正在构建服务镜像...${NC}"
    cd "${PROJECT_ROOT}" || exit 1
    docker-compose -f "${DOCKER_COMPOSE_FILE}" build
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}镜像构建成功${NC}"
    else
        echo -e "${RED}镜像构建失败${NC}"
        exit 1
    fi
}

# 显示帮助信息
show_help() {
    cat << EOF
用法: $0 [命令]

管理项目的 Docker 服务

命令:
  start                 启动所有服务
  stop                  停止所有服务
  restart               重启所有服务
  status                查看服务状态
  logs                  查看服务日志
  build                 构建服务镜像
  start-middleware      只启动中间件服务
  start-microservices   只启动微服务（需要先启动中间件）
  help                  显示此帮助信息

示例:
  $0 start              # 启动所有服务
  $0 start-middleware   # 只启动中间件
  $0 start-microservices # 只启动微服务
  $0 stop               # 停止所有服务
  $0 status             # 查看服务状态
EOF
}

# 主函数
main() {
    check_dependencies

    case "$1" in
        start)
            start_services
            ;;
        stop)
            stop_services
            ;;
        restart)
            restart_services
            ;;
        status)
            show_services_status
            ;;
        logs)
            show_services_logs
            ;;
        build)
            build_services
            ;;
        start-middleware)
            start_middleware
            ;;
        start-microservices)
            start_microservices
            ;;
        help | --help | -h)
            show_help
            ;;
        "")
            echo -e "${RED}错误: 请指定命令${NC}"
            show_help
            exit 1
            ;;
        *)
            echo -e "${RED}错误: 未知命令 '$1'${NC}"
            show_help
            exit 1
            ;;
    esac
}

# 执行主函数
main "$@"
