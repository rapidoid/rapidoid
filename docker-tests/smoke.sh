
#!/usr/bin/env bash
set -uo pipefail
IFS=$'\n\t'

export TAG=${TAG:-snapshot}

sudo service nginx stop || true

./simple-test.sh help --help

./simple-test-bg.sh verify verify
./simple-test-bg.sh run

./simple-test-bg.sh installer installer

./simple-test.sh cmd echo OK
./simple-test.sh pwd pwd
./simple-test.sh user whoami

./simple-fetch.sh ping 8888 /rapidoid/ping app.services=ping
./simple-fetch.sh status 8888 /rapidoid/status app.services=status id=rapidoid.xyz-123

./configuration.sh
./env-config.sh
./proxy.sh
./nobody.sh

./single-app.sh

./mysql.sh
./postgres.sh

./app-jar.sh

./cleanup.sh
