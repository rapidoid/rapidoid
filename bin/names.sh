#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

mvn clean

for f in `grep -lrw --include=*.java -e "public interface" rapidoid-http rapidoid-http-api rapidoid-app rapidoid-sql`; do
   ls -l $f
   perl -pi -e 's/(\(\s*|,\s*)(?:\@P\([^)]+?\)\s+)?([^)]+?)\s+([a-z]\w+?\b)/\1\@P("\3") \2 \3/g' $f
   perl -pi -e 's/\@P\([^)]+?\)\s*(.*?)(\(\@P\(|\(\s*\))/\1\2/' $f
done

bin/license-update.sh