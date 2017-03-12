#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

echo Will need sudo...
sudo echo Got sudo

docker-tests/cleanup.sh
mvn clean install

cd docker-tests
./retest.sh

cd ../examples
mvn clean install
