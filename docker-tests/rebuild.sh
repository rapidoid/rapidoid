#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

./cleanup.sh
cd ..
bin/docker.sh
