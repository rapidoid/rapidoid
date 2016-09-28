#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

export TAG=$1
echo "TARGET TAG: $TAG"

echo "Will need sudo!"
sudo echo "Thanks!"

rm -rf ./output/
mkdir output

./simple-test.sh help --help

./simple-test-bg.sh verify docker-self-verify
./simple-test-bg.sh run

./simple-test.sh cmd echo OK
./simple-test.sh pwd pwd
./simple-test.sh user whoami

./configuration.sh
./env-config.sh
./proxy.sh

./mysql.sh
./postgres.sh

./app-jar.sh

./simple-test-bg.sh app-service-overview app.services=overview
./simple-test-bg.sh admin-service-overview admin.services=overview

./simple-test-bg.sh app-service-application app.services=application
./simple-test-bg.sh admin-service-application admin.services=application

./simple-test-bg.sh app-service-lifecycle app.services=lifecycle
./simple-test-bg.sh admin-service-lifecycle admin.services=lifecycle

./simple-test-bg.sh app-service-jmx app.services=jmx
./simple-test-bg.sh admin-service-jmx admin.services=jmx

./simple-test-bg.sh app-service-metrics app.services=metrics
./simple-test-bg.sh admin-service-metrics admin.services=metrics

./simple-test-bg.sh app-service-deploy app.services=deploy
./simple-test-bg.sh admin-service-deploy admin.services=deploy

./simple-test-bg.sh app-service-status app.services=status
./simple-test-bg.sh admin-service-status admin.services=status

./simple-test-bg.sh app-service-ping app.services=ping
./simple-test-bg.sh admin-service-ping admin.services=ping

./simple-test-bg.sh app-service-auth app.services=auth
./simple-test-bg.sh admin-service-auth admin.services=auth

./simple-test-bg.sh app-service-oauth app.services=oauth
./simple-test-bg.sh admin-service-oauth admin.services=oauth

./simple-test-bg.sh app-service-center app.services=center
./simple-test-bg.sh admin-service-center admin.services=center

./simple-test-bg.sh app-service-entities app.services=entities
./simple-test-bg.sh admin-service-entities admin.services=entities

./simple-test-bg.sh app-service-foo app.services=foo
./simple-test-bg.sh admin-service-foo admin.services=foo

./simple-test-bg.sh app-service-welcome app.services=welcome
./simple-test-bg.sh admin-service-welcome admin.services=welcome

./simple-fetch.sh ping 8888 /_ping app.services=ping
./simple-fetch.sh status 8888 /_status app.services=status id=rapidoid.xyz-123

printf "\n - DONE\n\n"

docker ps -a
echo
