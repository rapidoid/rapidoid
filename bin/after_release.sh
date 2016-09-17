#!/usr/bin/env bash

OLD_VER=$1
NEW_VER=$2

CHANGES=`git status --porcelain`

printf "Bumping version from $OLD_VER to $NEW_VER...\n\n"

REPL="s/$OLD_VER/$NEW_VER/g"

for TARGET in examples/getting-started/pom.xml ../docker-rapidoid/Dockerfile
 do
    echo "Processing $TARGET"
    sed -i "$REPL" "$TARGET"
 done

echo

if [[ "$CHANGES" ]]; then
  echo "Dirty git index, cannot commit"
else
  echo "Clean git index, will commit..."
  git add examples/getting-started/pom.xml
  git commit -m "Bumped version from $OLD_VER to $NEW_VER."
fi
