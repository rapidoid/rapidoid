#!/usr/bin/env bash
set -uo pipefail
IFS=$'\n\t'

export TAG=$1
echo "TARGET TAG: $TAG"

rm -rf ./output/
mkdir output

./smoke.sh

./services.sh

./simple-fetch.sh ping 8888 /rapidoid/ping app.services=ping
./simple-fetch.sh status 8888 /rapidoid/status app.services=status id=rapidoid.xyz-123

printf "\n - DONE\n\n"

docker ps -a
echo
