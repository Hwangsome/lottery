#!/usr/bin/env bash
set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
COMPOSE_FILE="${PROJECT_ROOT}/docker-compose.yml"

compose() {
  if command -v docker-compose >/dev/null 2>&1; then
    docker-compose -f "${COMPOSE_FILE}" "$@"
  else
    docker compose -f "${COMPOSE_FILE}" "$@"
  fi
}

print_api_docs() {
  local host="http://localhost"
  local service
  local port
  local services=(
    "lottery-auth:8081"
    "lottery-user:8082"
    "lottery-activity:8083"
    "lottery-prize:8084"
    "lottery-points:8085"
    "lottery-notify:8086"
    "lottery-admin:8087"
  )

  echo
  echo "[INFO] 接口文档地址（Swagger / Knife4j）:"
  for service_port in "${services[@]}"; do
    service="${service_port%%:*}"
    port="${service_port##*:}"
    echo "  - ${service}"
    echo "    Knife4j: ${host}:${port}/doc.html"
    echo "    Swagger: ${host}:${port}/swagger-ui/index.html (兼容路径: /swagger-ui.html)"
    echo "    OpenAPI: ${host}:${port}/v3/api-docs"
  done

  echo "  - lottery-gateway"
  echo "    网关入口: ${host}:8080"
  echo "    Swagger 聚合: ${host}:8080/swagger-ui.html"
  echo "    OpenAPI: ${host}:8080/v3/api-docs"
}

check_deps() {
  if ! command -v docker >/dev/null 2>&1; then
    echo "[ERROR] docker 未安装"
    exit 1
  fi

  if ! command -v docker-compose >/dev/null 2>&1 && ! docker compose version >/dev/null 2>&1; then
    echo "[ERROR] docker compose / docker-compose 不可用"
    exit 1
  fi

  if ! docker info >/dev/null 2>&1; then
    echo "[ERROR] Docker 服务未运行"
    exit 1
  fi
}

check_jars() {
  local missing=0
  local pattern
  local patterns=(
    "${PROJECT_ROOT}/lottery-gateway/target/lottery-gateway-*.jar"
    "${PROJECT_ROOT}/lottery-auth/target/lottery-auth-*.jar"
    "${PROJECT_ROOT}/lottery-user/target/lottery-user-*.jar"
    "${PROJECT_ROOT}/lottery-activity/target/lottery-activity-*.jar"
    "${PROJECT_ROOT}/lottery-prize/target/lottery-prize-*.jar"
    "${PROJECT_ROOT}/lottery-points/target/lottery-points-*.jar"
    "${PROJECT_ROOT}/lottery-notify/target/lottery-notify-*.jar"
    "${PROJECT_ROOT}/lottery-admin/target/lottery-admin-*.jar"
  )

  for pattern in "${patterns[@]}"; do
    if ! compgen -G "${pattern}" >/dev/null; then
      echo "[ERROR] 未找到构建产物: ${pattern}"
      missing=1
    fi
  done

  if [[ ${missing} -ne 0 ]]; then
    echo "[ERROR] 请先在本地执行: mvn -DskipTests clean package"
    exit 1
  fi
}

main() {
  check_deps

  cd "${PROJECT_ROOT}"

  if [[ "${1:-}" == "--no-build" ]]; then
    echo "[INFO] 启动所有服务（不构建镜像）..."
    compose up -d
  else
    check_jars
    echo "[INFO] 启动所有服务（含构建镜像）..."
    compose up -d --build
  fi

  echo "[INFO] 当前服务状态:"
  compose ps

  print_api_docs
}

main "$@"
