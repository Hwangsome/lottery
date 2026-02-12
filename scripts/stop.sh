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

check_deps() {
  if ! command -v docker >/dev/null 2>&1; then
    echo "[ERROR] docker 未安装"
    exit 1
  fi

  if ! command -v docker-compose >/dev/null 2>&1 && ! docker compose version >/dev/null 2>&1; then
    echo "[ERROR] docker compose / docker-compose 不可用"
    exit 1
  fi
}

main() {
  check_deps

  cd "${PROJECT_ROOT}"

  if [[ "${1:-}" == "--volumes" ]]; then
    echo "[INFO] 停止并删除服务、网络、数据卷..."
    compose down -v
  else
    echo "[INFO] 停止并删除服务、网络..."
    compose down
  fi
}

main "$@"
