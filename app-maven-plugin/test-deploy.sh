#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

VERSION=$(
    grep -Po "<version>\d+\.\d+\.\d+-SNAPSHOT</version>" pom.xml \
    | head -n 1 \
    | grep -Po "\d+\.\d+\.\d+-SNAPSHOT"
)

echo "Testing version: $VERSION"

mvn install
cd ../examples/getting-started
mvn clean org.rapidoid:app:${VERSION}:deploy
