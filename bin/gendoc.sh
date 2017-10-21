#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

mvn generate-resources
cp target/generated-docs/index.html ../rapidoid.github.io/docs.html
