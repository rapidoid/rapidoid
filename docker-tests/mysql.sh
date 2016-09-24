#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

./cleanup.sh

rm -f mysql-*.txt

printf "\n - Testing MYSQL (tag=$TAG)\n\n"

MYSQL_ID=$(sudo docker run -d -e MYSQL_ROOT_PASSWORD=db-pass mysql)

sudo docker run \
    -e "UNIFORM_OUTPUT=true" \
    -p 8888:8888 \
    --privileged \
    --link $MYSQL_ID:mysql \
    rapidoid/rapidoid:$TAG \
    profiles=mysql \
    jdbc.password=db-pass \
    '/users <= SELECT user FROM mysql.user' \
    > output/mysql.txt 2>&1 &

./wait-for.sh 8888

sleep 60 # give MySQL some time to initialize

./http-get.sh mysql-users 8888 /users

./cleanup.sh
