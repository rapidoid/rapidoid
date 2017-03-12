#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

OLD_VER=$1
NEW_VER=$2

RAP_DIRTY=`git status --porcelain`

printf "Bumping version from $OLD_VER to $NEW_VER...\n\n"

REPL="s/$OLD_VER/$NEW_VER/g"

for TARGET in $(find examples -name pom.xml)
do
    echo "Processing $TARGET"
    sed -i "$REPL" "$TARGET"
    git add "$TARGET"
done

echo

if [[ "$RAP_DIRTY" ]]; then
  echo "Dirty git index, cannot commit"
else
  echo "Clean git index, will commit..."
  git commit -m "Bumped Rapidoid version from $OLD_VER to $NEW_VER."
fi

printf "\n--- Going to rapidoid.github.io ---\n\n"

cd ../rapidoid.github.io

RGH_DIRTY=`git status --porcelain`

if [[ "$RGH_DIRTY" ]]; then
  echo "Dirty git index, cannot apply changes"
  git status
else
  echo "Clean git index, will apply changes and commit..."
  sed -i "$REPL" "$TARGET" *.html
  git add *.html
  git add *.css
  git add *.js
  git commit -m "Bumped version from $OLD_VER to $NEW_VER."
fi
