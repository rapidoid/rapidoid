#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

echo Building rapidoid/rapidoid:snapshot
bin/quick-install.sh
cp rapidoid-standalone/target/rapidoid-standalone-*-SNAPSHOT.jar docker/rapidoid.jar
cd docker
ls -l rapidoid.jar
docker build -t rapidoid/rapidoid:snapshot .
docker rmi $(docker images -f "dangling=true" -q)
docker images -a
