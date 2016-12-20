#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

for i in $(seq 1 10000); do
  echo ============================ $i ================================
  mvn clean verify
done
