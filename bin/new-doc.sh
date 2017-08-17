#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

PKG=$1
CLS="${2}Test"

echo "Adding new doc/example '$PKG', class $CLS"

# ASCIIDOC

cd asciidoc

cp blank.adoc "${PKG}.adoc"

sed -Ei "s/(blank|Blank)/$PKG/g" "${PKG}.adoc"

printf '\ninclude::'${PKG}'.adoc[]\n' >> topics.adoc

cd ..

# JAVA TESTS

cd rapidoid-integration-tests/src/test/java/org/rapidoid/docs/
cp -R blank $PKG

cd "$PKG"

sed -i "s/blank/$PKG/g" *

sed -i "s/BlankTest/$CLS/g" BlankTest.java
mv BlankTest.java "${CLS}.java"

echo && ls
