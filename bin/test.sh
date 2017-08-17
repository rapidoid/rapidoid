#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

rm -rf /tmp/tmp-build
cp -R . /tmp/tmp-build
cd /tmp/tmp-build
pwd
mvn clean install
cd ..
rm -rf /tmp/tmp-build