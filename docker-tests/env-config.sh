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
    -e "RAPIDOID_PORT=4444" \
    -e "APP_SERVICES=status" \
    -e "ADMIN_SERVICES=overview" \
    rapidoid/rapidoid:$TAG \
    abc=123 \
    > output/env-config.txt 2>&1 &

./wait-for.sh 4444

./http-get.sh env-config-req 4444 /rapidoid/status

./cleanup.sh
