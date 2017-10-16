#!/usr/bin/env bash
set -uo pipefail
IFS=$'\n\t'

export TAG=${1:-snapshot}
echo "TARGET TAG: $TAG"

rm -rf ./output/
mkdir output

./smoke.sh

./services.sh

printf "\n - DONE\n\n"

docker ps -a
echo
