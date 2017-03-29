#!/usr/bin/env bash

mvn generate-resources
cp target/generated-docs/index.html ../rapidoid.github.io/docs.html
