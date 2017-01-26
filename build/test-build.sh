#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

mvn install
cd ../examples/getting-started
mvn clean org.rapidoid:build:uber-jar

#cd ../guice-integration
#mvn clean org.rapidoid:build:uber-jar

cd target
ls -l
cd ..

unzip -l target/app.jar
java -jar target/app.jar
