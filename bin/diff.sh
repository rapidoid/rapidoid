#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

git diff -U0 | grep '^[+-]' | grep -Ev '^(--- a/|\+\+\+ b/)' > diff.txt
