#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

PKG=$1
CLS="${2}Test"

echo "Adding new doc/example '$PKG', class $CLS"

cd rapidoid-integration-tests/src/test/java/org/rapidoid/docs/
cp -R blank $PKG

cd "$PKG"

sed -i "s/blank/$PKG/g" *

sed -i "s/BlankTest/$CLS/g" BlankTest.java
mv BlankTest.java "${CLS}.java"

echo && ls
