#!/usr/bin/env bash
bin/pkgs-list.sh | sort| grep "org.rapidoid." > rapidoid-commons/src/main/resources/rapidoid-classes.txt
cat rapidoid-commons/src/main/resources/rapidoid-classes.txt