#!/bin/bash

maven() {
    if hash mvn-custom 2>/dev/null; then
        mvn-custom "$@"
    else
        mvn "$@"
    fi
}

maven clean package -U

if [[ $@ != 0 ]]; then
	exit 1
fi
maven docker:build
docker save beholder:latest > beholder-docker.tar.gz
echo `git log -n 1 --pretty=format:"%H"` > revision.txt
