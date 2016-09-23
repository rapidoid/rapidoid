#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

./cleanup.sh

printf "\n - Testing ENV-CONFIG (tag=$TAG)\n\n"

docker run \
    --net host \
    -v $(pwd)/app2:/app \
    -u nobody \
    -e "UNIFORM_OUTPUT=true" \
    -e "ROOT=/app/the-root" \
    -e "CONFIG=my-config" \
    -e "ON_PORT=5555" \
    rapidoid/rapidoid:$TAG \
    app.services=status \
    > output/env-config.txt 2>&1 &

./wait-for.sh 5555

./http-get.sh env-config-req 5555 /_status

./cleanup.sh
