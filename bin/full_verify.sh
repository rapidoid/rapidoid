#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

export RAPIDOID_TEST_HEAVY=true
mvn clean install

cd ../examples
mvn clean install
