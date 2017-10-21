#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

./cleanup.sh

printf "\n - Testing PROXY (tag=$TAG)\n\n"

docker run \
    --net=host \
    -e "UNIFORM_OUTPUT=true" \
    rapidoid/rapidoid:$TAG \
    '/app->http://localhost:8080,http://localhost:9090' \
    '/->http://localhost:9090,http://localhost:8080' \
    rapidoid.port=80 \
    > output/proxy.txt 2>&1 &

sleep 3

docker run -d \
    --net=host \
    -e "UNIFORM_OUTPUT=true" \
    rapidoid/rapidoid:$TAG \
    id=app1 \
    rapidoid.port=8080 \
    app.services=status

docker run -d \
    --net=host \
    -e "UNIFORM_OUTPUT=true" \
    rapidoid/rapidoid:$TAG \
    id=app2 \
    rapidoid.port=9090 \
    app.services=status

./wait-for.sh 8080
./wait-for.sh 9090

./http-get.sh app-proxy-req1 80 /app/rapidoid/status
./http-get.sh app-proxy-req2 80 /app/rapidoid/status
./http-get.sh app-proxy-req3 80 /app/rapidoid/status
./http-get.sh app-proxy-req4 80 /app/rapidoid/status
./http-get.sh app-proxy-req5 80 /app/rapidoid/status

./http-get.sh proxy-req1 80 /rapidoid/status
./http-get.sh proxy-req2 80 /rapidoid/status
./http-get.sh proxy-req3 80 /rapidoid/status

./cleanup.sh
