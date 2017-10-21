#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

set_versions() {
    OLD_VER=$1
    NEW_VER=$2

    SNAPSHOT_VER=$(
        grep -Po "<version>\d.\d.\d-SNAPSHOT</version>" pom.xml \
        | head -n 1 \
        | grep -Po "\d.\d.\d-SNAPSHOT"
    )
}

initialize() {
    set_versions $(
        git ls-remote --tags https://github.com/rapidoid/rapidoid.git \
            | cut -d/ -f3 \
            | grep -vE -- 'rapidoid-|\^' \
            | sort -V \
            | tail -2
    )
}

wait_enter() {
    printf "\nPress ENTER to continue...\n\n"
    read
}

confirm() {
    echo
    echo "OLD version: $OLD_VER"
    echo "NEW version: $NEW_VER"
    echo "SNAPSHOT version: $SNAPSHOT_VER"
    wait_enter
}

process_examples() {
    local git_dirty=`git status --porcelain`

    for target in $(find examples -name pom.xml)
    do
        echo "Processing $target"
        sed -i "s/${NEW_VER}-SNAPSHOT/${SNAPSHOT_VER}/g" "$target"
        git add "$target"
    done

    git_commit "$git_dirty" "Bumped the version of the examples"
}

process_docs() {
    local git_dirty=`git status --porcelain`

    sed -i "s/${OLD_VER}/${NEW_VER}/g" *.html
    git add *.html

    git_commit "$git_dirty" "Bumped version"
}

process_docker() {
    local git_dirty=`git status --porcelain`

    sed -ri 's/^(ENV RAPIDOID_VERSION) .*/\1 '"${NEW_VER}"'/' Dockerfile
    git add Dockerfile

    git_commit "$git_dirty" "Rapidoid v${NEW_VER}"

    echo "Will push..."
    wait_enter
    git push
}

process_official_images() {
    local git_dirty=`git status --porcelain`

    printf "Rebasing on upstream...\n\n"
    git pull --rebase upstream master

    readonly OLD_VER_MID=${OLD_VER:0:3}
    readonly NEW_VER_MID=${NEW_VER:0:3}

    printf "\nMinor releases: '$OLD_VER_MID' and '$NEW_VER_MID'\n\n"

    echo
    read -p "Enter COMMIT ID: " commit_id
    echo

    sed -i "s/${OLD_VER}/${NEW_VER}/g" library/rapidoid
    sed -i "s/${OLD_VER_MID}/${NEW_VER_MID}/g" library/rapidoid
    sed -ri "s/GitCommit:\s\w+/GitCommit: ${commit_id}/g" library/rapidoid

    git diff
    printf "\nWill commit and push...\n\n"
    wait_enter

    git add library/rapidoid
    git_commit "$git_dirty" "Rapidoid v${NEW_VER}"
    git push

    echo === VISIT https://github.com/rapidoid/docker-official-images ===
}

git_commit() {
    local dirty=$1
    local msg=$2

    if [[ "$dirty" ]]; then
      printf "\n !!! Dirty git index, cannot commit !!!\n\n"
    else
      echo "Clean git index, will commit..."
      git commit -m "$msg"
      printf "\n - Latest commit:\n\n"
      git log -n 1
      echo
      git diff HEAD~1 HEAD
    fi
}

do_processing() {
    printf "\n--- PROCESSING EXAMPLES...\n\n"
    process_examples || true
    wait_enter

    printf "\n--- PROCESSING rapidoid.github.io...\n\n"
    cd ../rapidoid.github.io
    process_docs || true
    wait_enter

    printf "\n--- PROCESSING docker-rapidoid...\n\n"
    cd ../docker-rapidoid
    process_docker || true
    wait_enter

    printf "\n--- PROCESSING docker-official-images...\n\n"
    cd ../docker-official-images/
    process_official_images
    wait_enter

    echo DONE
}

initialize && confirm && do_processing