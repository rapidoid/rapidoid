#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

echo "Clean-up"

docker kill $(docker ps -q) > /dev/null 2>&1 || true
docker rm $(docker ps -a -q) > /dev/null 2>&1  || true

rm -rf app-mini/*
rm -f app3/app.jar
