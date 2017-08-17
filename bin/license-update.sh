#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

mvn license:update-file-header
