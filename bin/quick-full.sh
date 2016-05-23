#!/bin/bash
set -euo pipefail
IFS=$'\n\t'

mvn clean install -DskipTests=true -Pfull
cd ..
ls -l *.jar
