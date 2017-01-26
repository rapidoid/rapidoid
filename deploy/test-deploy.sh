#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

mvn install
cd ../examples/getting-started
mvn clean org.rapidoid:deploy:jar

cd target
ls -l
cd ..

java -cp target/app.jar com.example.Main
