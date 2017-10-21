#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

mvn clean install -DskipTests=true -Pfull
ls -l rapidoid.jar
