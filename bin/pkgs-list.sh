#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

for f in `grep -lrw --include=*.java . | grep -v "src/test"`; do
   echo $f | perl -p -e 's/^(?:rapidoid|app-maven-plugin|commons|networking).*src\/main\/java\///g' | perl -p -e 's/.java//g' | perl -p -e 's/\//./g'
done