#!/usr/bin/env bash

docker-tests/cleanup.sh
mvn clean install && bin/verify.sh
