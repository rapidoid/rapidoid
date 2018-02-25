#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

echo Copying On into Admin

cp rapidoid-rest/src/main/java/org/rapidoid/setup/On.java rapidoid-rest/src/main/java/org/rapidoid/setup/Admin.java

perl -pi -e 's/\bON\b/ADMIN/' rapidoid-rest/src/main/java/org/rapidoid/setup/Admin.java
perl -pi -e 's/\bOn\b/Admin/' rapidoid-rest/src/main/java/org/rapidoid/setup/Admin.java
perl -pi -e 's/\Q.on()\E/.admin()/' rapidoid-rest/src/main/java/org/rapidoid/setup/Admin.java
perl -pi -e 's/\b4\.3\.0\b/5.1.0/' rapidoid-rest/src/main/java/org/rapidoid/setup/Admin.java
