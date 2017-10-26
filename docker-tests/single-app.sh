#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

./cleanup.sh

docker run \
    --net=host \
    -e uniform_output=true \
    -v $(pwd)/single-app1:/app \
    rapidoid/rapidoid:$TAG \
    > output/single-app.txt 2>&1 &

./wait-for.sh 8888

./http-get.sh single-app-index 8888 /

./cleanup.sh
