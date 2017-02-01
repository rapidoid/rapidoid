#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo Building rapidoid/rapidoid:snapshot

cd ${DIR}/..
rm -f docker/rapidoid.jar
cp ../docker-rapidoid/entrypoint.sh docker

bin/quick-install.sh
cp rapidoid-platform/target/rapidoid-platform-*-SNAPSHOT.jar docker/rapidoid.jar

cd docker
ls -l .

docker build -t rapidoid/rapidoid:snapshot .
docker rmi $(docker images -f "dangling=true" -q) || echo "Nothing to remove"

rm -f entrypoint.sh
rm rapidoid.jar

echo rapidoid/rapidoid:snapshot was built successfully
