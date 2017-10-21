#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

readonly TAG=${1:-latest}
echo Building tag ${TAG}

read -p "Delete all containers and images before building? (y/n) " Y_N
echo

if [[ ${Y_N} =~ ^[Yy]$ ]]
then
    docker ps -a -q | xargs docker rm || true
    docker images -q | xargs docker rmi || true
fi

cd ../docker-rapidoid

docker build -t rapidoid/rapidoid:${TAG} .
docker images -a
