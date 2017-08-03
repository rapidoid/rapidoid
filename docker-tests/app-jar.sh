#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

./cleanup.sh

printf "\n - BUILDING app.jar\n\n"

cd ../examples/getting-started
mvn clean org.rapidoid:app:build && cp target/app.jar ../../docker-tests/app3

cd ../../docker-tests
chmod o+r app3/app.jar

printf "\n - Testing APP-JAR (tag=$TAG)\n\n"

DB_ID=$(docker run -d -e MYSQL_ROOT_PASSWORD=db-pass -e MYSQL_DATABASE=rapidoid mysql)

sudo docker run \
    -e uniform_output=true \
    -e JDBC_PASSWORD=db-pass \
    -e profiles=mysql \
    -p 8888:8888 \
    -p 8080:8080 \
    -v $(pwd)/app3:/app \
    --link $DB_ID:mysql \
    rapidoid/rapidoid:$TAG \
    app.services=welcome \
    admin.services=center \
    app.path=com.example \
    '/users <= SELECT user FROM mysql.user' \
    > output/app-jar.txt 2>&1 &

sleep 60 # give MySQL some time to initialize

./wait-for.sh 8080

./http-get.sh app-jar-index 8080 /
./http-get.sh app-jar-manage 8080 /manage
./http-get.sh app-jar-books 8080 /books

./cleanup.sh
