#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

mvn install

cd ../examples/hello-world
#cd ../examples/getting-started
#cd ../examples/guice-integration
#cd ../rapidoid-platform

mvn clean org.rapidoid:app:build

cd target
ls -l
cd ..

unzip -l target/app.jar
java -jar target/app.jar
