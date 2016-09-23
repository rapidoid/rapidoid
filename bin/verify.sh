#!/usr/bin/env bash
set -euo pipefail

docker-tests/cleanup.sh
bin/docker.sh
bin/retest.sh
