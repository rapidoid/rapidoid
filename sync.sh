#!/usr/bin/env bash
# add the main Rapidoid repository as upstream
git remote add upstream https://github.com/rapidoid/rapidoid.git

# pull in case there are some changes in the origin
git pull origin

# fetch all the branches of the main Rapidoid repository
git fetch upstream

# checkout the local "master" branch
git checkout master

# merge the local "master" branch with the "master" branch of the main Rapidoid repository 
git merge upstream/master
