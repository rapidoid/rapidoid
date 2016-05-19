#!/usr/bin/env bash
cp rapidoid.jar docker/
cd docker
ls -l rapidoid.jar
sudo docker build -t rapidoid/rapidoid .
