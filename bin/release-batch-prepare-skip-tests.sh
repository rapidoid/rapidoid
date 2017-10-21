#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

mvn --batch-mode release:prepare -Darguments="-DskipTests" -Psrc-and-doc
