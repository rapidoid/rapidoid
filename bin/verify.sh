#!/usr/bin/env bash
set -euo pipefail

echo Will need sudo...
sudo echo ""

docker-tests/cleanup.sh
bin/docker.sh

cd docker-tests
./retest.sh
