#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

./cleanup.sh

printf "\n - Testing NOBODY (tag=$TAG)\n\n"

docker run \
    -u nobody \
    --net host \
    -e "UNIFORM_OUTPUT=true" \
    rapidoid/rapidoid:$TAG \
    app.services=status \
    id=nobody \
    > output/nobody.txt 2>&1 &

./wait-for.sh 8888

./http-get.sh nobody-req 8888 /rapidoid/status

./cleanup.sh
