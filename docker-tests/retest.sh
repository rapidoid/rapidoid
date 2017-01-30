#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

./rebuild.sh
./tests.sh snapshot
