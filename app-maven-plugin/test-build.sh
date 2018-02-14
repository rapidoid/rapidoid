#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

VERSION=$(
    grep -Po "<version>\d+\.\d+\.\d+-SNAPSHOT</version>" pom.xml \
    | head -n 1 \
    | grep -Po "\d+\.\d+\.\d+-SNAPSHOT"
)

echo "Testing version: $VERSION"

mvn install

cd ../examples/hello-world
#cd ../examples/getting-started
#cd ../examples/guice-integration

mvn clean org.rapidoid:app:${VERSION}:build

cd target
ls -l
cd ..

unzip -l target/app.jar
java -jar target/app.jar
