#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

./cleanup.sh

printf "\n - Testing PROXY\n\n"

docker run -d \
    --net=host \
    -e "UNIFORM_OUTPUT=true" \
    --name proxy \
    rapidoid/rapidoid:$TAG \
    '/app1->https://localhost:8080' \
    '/app2->https://localhost:9090' \
    '/->https://https://localhost:8080,http://upstream2:9090' \
    on.port=80 \

docker run -d \
    --net=host \
    -e "UNIFORM_OUTPUT=true" \
    --name app1 \
    rapidoid/rapidoid:$TAG \
    on.port=8080 \
    secret=secret1 \
    users.admin.password=my-passwd

docker run -d \
    --net=host \
    -e "UNIFORM_OUTPUT=true" \
    --name app2 \
    rapidoid/rapidoid:$TAG \
    on.port=9090 \
    secret=secret1 \
    users.admin.password=my-passwd

sleep 5

curl -i  --raw 'http://localhost:80/'
curl -i  --raw 'http://localhost:80/'
curl -i  --raw 'http://localhost:80/'

./cleanup.sh
