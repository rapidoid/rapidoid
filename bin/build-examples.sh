#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

echo Building the examples...

cd examples

mvn clean install
