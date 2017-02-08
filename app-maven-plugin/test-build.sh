#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

mvn install
cd ../examples/getting-started
mvn clean org.rapidoid:app:build

#cd ../guice-integration
#mvn clean org.rapidoid:app:build

cd target
ls -l
cd ..

unzip -l target/app.jar
java -jar target/app.jar
