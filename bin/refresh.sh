#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

bin/pkgs.sh

git reset
git add rapidoid-commons/src/main/resources/rapidoid-classes.txt
git commit -m "Updated list of classes."
git log -n 1
