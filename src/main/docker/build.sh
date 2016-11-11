#!/bin/bash

set -e

TARGET_PATH=../../../target
VERSION=1.0-SNAPSHOT

cp $TARGET_PATH/beholder-web-$VERSION.war beholder-web.war
cp $TARGET_PATH/jetty.jndi .
cp $TARGET_PATH/initExt.sh .

sudo docker build -t beholder-web .

rm beholder-web.war
rm jetty.jndi
rm initExt.sh
