#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

./cleanup.sh

printf "\n - Testing MYSQL (tag=$TAG)\n\n"

docker run -d --name mysql -e MYSQL_ROOT_PASSWORD=db-pass mysql

sleep 60 # give MySQL some time to initialize

sudo docker run \
    -e "UNIFORM_OUTPUT=true" \
    -p 8888:8888 \
    --privileged \
    --link mysql:db \
    rapidoid/rapidoid:$TAG \
    profiles=mysql \
    jdbc.host=db \
    jdbc.password=db-pass \
    '/users <= SELECT distinct(user) FROM mysql.user' \
    > output/mysql.txt 2>&1 &

./wait-for.sh 8888

./http-get.sh mysql-users 8888 /users

./cleanup.sh
