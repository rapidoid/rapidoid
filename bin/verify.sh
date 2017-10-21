#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

cd docker-tests
./retest.sh
