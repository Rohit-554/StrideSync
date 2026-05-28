#!/bin/bash
set -euo pipefail

cd "$(dirname "$0")/.."

docker compose up -d postgres
./gradlew :server:run
