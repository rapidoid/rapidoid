#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

echo Will need sudo...
sudo echo Got sudo

export RAPIDOID_TEST_HEAVY=true
mvn clean install

cd ../examples
mvn clean install
