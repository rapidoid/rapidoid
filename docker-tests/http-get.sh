#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

URL="http://localhost:$2$3"
printf "GET $URL\n\n"
curl -i --raw "$URL" > output/$1-result.txt 2> /dev/null
