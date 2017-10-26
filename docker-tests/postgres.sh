#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

./cleanup.sh

printf "\n - Testing POSTGRES (tag=$TAG)\n\n"

docker run -d --name postgres -e POSTGRES_PASSWORD=db-pass postgres

sleep 20 # give PostgreSQL some time to initialize

sudo docker run \
    -e UNIFORM_OUTPUT=true \
    -e JDBC_HOST=db \
    -e JDBC_PASSWORD=db-pass \
    -p 8888:8888 \
    --link postgres:db \
    rapidoid/rapidoid:$TAG \
    profiles=postgres \
    '/users <= SELECT usename from pg_shadow' \
    > output/postgres.txt 2>&1 &

./wait-for.sh 8888

./http-get.sh postgres-users 8888 /users

./cleanup.sh
