#!/usr/bin/env bash
for f in `grep -lrw --include=*.java . | grep -v "src/test"`; do
   echo $f | perl -p -e 's/^(?:rapidoid|deploy|build).*src\/main\/java\///g' | perl -p -e 's/.java//g' | perl -p -e 's/\//./g'
done