#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

for f in `grep -lrw --include=*.html '{'`; do
   echo $f
   perl -pi -e 's/\{\{\{([\w\.\-]+)\}\}\}/\@{\1}/g' $f
   perl   -pi -e 's/\{\{([\w\.\-]+)\}\}/\${\1}/g' $f
done