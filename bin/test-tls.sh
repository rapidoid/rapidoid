#!/usr/bin/env bash

export TLS_ENABLED=true
export TLS_KEYSTORE=/tmp/keystore

mvn clean install
