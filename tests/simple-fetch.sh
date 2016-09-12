#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

NAME=$1
PORT=$2
URI=$3
shift; shift; shift

printf "\n - $NAME (port=$PORT, uri=$URI, args=$*)\n"
./cleanup.sh

docker run --net=host -e "UNIFORM_OUTPUT=true" rapidoid/rapidoid:$TAG "$@" > output/$NAME.txt 2>&1 &

./wait-for.sh "$PORT"

./http-get.sh "$NAME" "$PORT" "$URI"

./cleanup.sh
