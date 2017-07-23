#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

./cleanup.sh

printf "\n - Testing ENV-CONFIG (tag=$TAG)\n\n"

docker run \
    --net host \
    -v $(pwd)/app2:/app \
    -e "UNIFORM_OUTPUT=true" \
    -e "ROOT=/app/the-root" \
    -e "CONFIG=my-config" \
    -e "ON_PORT=5555" \
    -e "RAPIDOID_PORT=9999" \
    -e "APP_SERVICES=status" \
    -e "ADMIN_SERVICES=overview" \
    rapidoid/rapidoid:$TAG \
    abc=123 \
    > output/env-config.txt 2>&1 &

./wait-for.sh 9999

./http-get.sh env-config-req 9999 /rapidoid/status

./cleanup.sh
