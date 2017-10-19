#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

echo Will need sudo...
sudo echo Got sudo

docker-tests/cleanup.sh

export RAPIDOID_TEST_HEAVY=true
mvn clean install

cd docker-tests
./retest.sh

cd ../examples
mvn clean install
