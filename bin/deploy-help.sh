#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

echo http://central.sonatype.org/pages/ossrh-guide.html
 
echo gpg --gen-key
echo gpg --list-keys
echo gpg --list-secret-keys
echo gpg --keyserver hkp://pgp.mit.edu --send-keys ABCDEFGH
