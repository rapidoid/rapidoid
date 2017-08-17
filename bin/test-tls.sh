#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

export TLS_ENABLED=true
export TLS_KEYSTORE=/tmp/keystore

mvn clean install
