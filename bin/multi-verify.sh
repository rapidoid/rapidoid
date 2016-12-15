#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

for i in $(seq 1 100); do
  mvn clean verify
done
