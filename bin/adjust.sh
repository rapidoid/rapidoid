#!/usr/bin/env bash
set -euo pipefail

mvn clean install -DADJUST_TESTS=true
