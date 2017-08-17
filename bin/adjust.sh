#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

mvn clean install -DADJUST_TESTS=true
