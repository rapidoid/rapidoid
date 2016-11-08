#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

./cleanup.sh

printf "\n - Running minimal Rapidoid container (tag=$TAG)\n\n"

sudo docker run -it \
    --restart=always \
    -p 8888:8888 \
    -v $(pwd)/app-mini:/app \
    rapidoid/rapidoid:$TAG \
    secret=not-a-big-secret \
    admin.services=center \
    users.admin.password=aaa \
    app.cdn=false
