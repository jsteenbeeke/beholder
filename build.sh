#!/bin/bash

maven() {
    if hash mvn-custom 2>/dev/null; then
        mvn-custom "$@"
    else
        mvn "$@"
    fi
}

maven clean package -U
maven docker:build
docker save jeroensteenbeeke_com/beholder:latest > beholder-docker.tar.gz
