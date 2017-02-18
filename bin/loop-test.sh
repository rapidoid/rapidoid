#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

DIR=$1
TEST=$2

cd $DIR
for i in $(seq 1 10000000)
do
  printf "\n\n============================== $i ==============================\n\n"
  mvn test -Dtest=$2
done
