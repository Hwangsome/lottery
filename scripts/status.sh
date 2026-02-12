#!/usr/bin/env bash
set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
COMPOSE_FILE="${PROJECT_ROOT}/docker-compose.yml"

if command -v docker-compose >/dev/null 2>&1; then
  docker-compose -f "${COMPOSE_FILE}" ps
else
  docker compose -f "${COMPOSE_FILE}" ps
fi
