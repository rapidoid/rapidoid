#!/usr/bin/env bash

echo Will need sudo...
sudo echo Got sudo

docker-tests/cleanup.sh
mvn clean install && docker-tests/retest.sh
