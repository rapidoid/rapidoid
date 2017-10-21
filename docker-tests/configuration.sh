#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

./cleanup.sh

printf "\n - Testing CONFIGURATION (tag=$TAG)\n\n"

docker run \
    --net host \
    -v $(pwd)/app1:/app \
    -e "UNIFORM_OUTPUT=true" \
    rapidoid/rapidoid:$TAG \
    app.services=status \
    > output/configuration.txt 2>&1 &

./wait-for.sh 4444

./http-get.sh configuration-req 4444 /rapidoid/status

./cleanup.sh
