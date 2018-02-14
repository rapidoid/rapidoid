#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

git diff -U0 HEAD HEAD~1 | grep '^[+-]' | grep -Ev '^(--- a/|\+\+\+ b/)' > diff.txt

sort diff.txt | uniq
