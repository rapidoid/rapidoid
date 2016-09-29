#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

echo Building the getting-started example

cd examples/getting-started

mvn clean install -Pfull

cp target/app-full.jar ../../docker-tests/app3/app.jar
