#!/usr/bin/env bash
set -euo pipefail

mvn clean install -DadjustTests=true
