#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

LOCATION=$1
GOAL=$2

mvn install
cd $LOCATION
mvn clean org.rapidoid:deploy:$GOAL

cd target
ls -l
