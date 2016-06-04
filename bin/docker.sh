#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

echo Building version $1
mvn clean install -DskipTests=true -Pfull
cp rapidoid.jar docker/
cd docker
ls -l rapidoid.jar
sudo docker build -t rapidoid/rapidoid:$1 .
