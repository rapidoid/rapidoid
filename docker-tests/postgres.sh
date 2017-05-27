#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

./cleanup.sh

printf "\n - Testing POSTGRES (tag=$TAG)\n\n"

DB_ID=$(docker run -e POSTGRES_PASSWORD=db-pass -d postgres)

sudo docker run \
    -e UNIFORM_OUTPUT=true \
    -e JDBC_HOST=db \
    -e JDBC_PASSWORD=db-pass \
    -p 8888:8888 \
    --link $DB_ID:db \
    rapidoid/rapidoid:$TAG \
    profiles=postgres \
    '/users <= SELECT usename from pg_shadow' \
    > output/postgres.txt 2>&1 &

./wait-for.sh 8888

sleep 20 # give PostgreSQL some time to initialize

./http-get.sh postgres 8888 /users

./cleanup.sh
