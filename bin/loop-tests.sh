#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

for i in $(seq 1 10000000)
do
  printf "\n\n============================== $i ==============================\n\n"
  mvn clean verify
done
