#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

cd /tmp

VER=$1
wget https://repo1.maven.org/maven2/org/rapidoid/rapidoid-platform/$VER/rapidoid-platform-$VER.jar.asc
wget https://repo1.maven.org/maven2/org/rapidoid/rapidoid-platform/$VER/rapidoid-platform-$VER.jar
