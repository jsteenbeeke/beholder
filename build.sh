#!/bin/bash
mvn-custom clean package docker:build
docker save jeroensteenbeeke_com/beholder:latest > beholder-docker.tar.gz
