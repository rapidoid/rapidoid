#!/usr/bin/env bash

echo Will need sudo...
sudo echo Got sudo

docker-tests/cleanup.sh
mvn clean install && bin/verify.sh
