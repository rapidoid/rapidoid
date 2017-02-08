#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

mvn install
cd ../examples/getting-started
mvn clean org.rapidoid:app:deploy
