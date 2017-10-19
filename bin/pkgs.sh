#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

bin/pkgs-list.sh | sort| grep "org.rapidoid." > commons/rapidoid-commons/src/main/resources/rapidoid-classes.txt
cat commons/rapidoid-commons/src/main/resources/rapidoid-classes.txt