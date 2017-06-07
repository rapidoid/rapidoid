#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

echo Will need sudo...
sudo echo Got sudo

./rebuild.sh

export TAG=snapshot

./smoke.sh
