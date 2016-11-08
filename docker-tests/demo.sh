#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

./cleanup.sh

printf "\n - Testing APP-JAR (tag=$TAG)\n\n"

DB_ID=$(docker run -d -e MYSQL_ROOT_PASSWORD=db-pass -e MYSQL_DATABASE=rapidoid mysql)

sudo docker run -it --rm \
    -e HIBERNATE_CONNECTION_PASSWORD=db-pass \
    -e JDBC_PASSWORD=db-pass \
    -e profiles=mysql \
    -p 8888:8888 \
    -u nobody \
    -v $(pwd)/app3:/app \
    --link $DB_ID:mysql \
    rapidoid/rapidoid:$TAG \
    app.services=welcome \
    admin.services=center \
    app.path=com.example \
    users.admin.password=aaa \
    app.cdn=false \
    '/users <= SELECT user FROM mysql.user'
