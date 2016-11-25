#!/usr/bin/env bash

mvn clean package -Pfull && java -jar target/app.jar
