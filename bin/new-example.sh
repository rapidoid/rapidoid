#!/usr/bin/env bash

NAME=$1
echo Adding new example: "$NAME"

cd examples
cp -R hello-world $NAME

sed -i "s/<!--...-->/<module>$NAME<\/module>\n\t\t<!--...-->/g" pom.xml

cd $NAME

sed -i "s/hello-world/$NAME/g" pom.xml

rm hello-world.iml

mvn clean
