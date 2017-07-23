#!/usr/bin/env bash
set -uo pipefail
IFS=$'\n\t'

sudo service nginx stop || true

./simple-test.sh help --help

./simple-test-bg.sh verify docker-self-verify
./simple-test-bg.sh run

./simple-test-bg.sh installer installer

./simple-test.sh cmd echo OK
./simple-test.sh pwd pwd
./simple-test.sh user whoami

./configuration.sh
./env-config.sh
./proxy.sh
./nobody.sh

./mysql.sh
./postgres.sh

./app-jar.sh

./cleanup.sh
