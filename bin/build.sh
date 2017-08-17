#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

export DOCS=true

mvn clean install
