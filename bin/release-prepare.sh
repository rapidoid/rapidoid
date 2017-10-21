#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

mvn release:clean release:prepare
