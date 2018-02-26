#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

mvn generate-resources

cp target/generated-docs/index.html ../rapidoid.github.io/docs-6.0.html

cp target/generated-docs/notes.html ../rapidoid.github.io/release-notes.html
