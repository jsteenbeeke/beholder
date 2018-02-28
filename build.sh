#!/bin/bash

maven() {
    if hash mvn-custom 2>/dev/null; then
        mvn-custom "$@"
    else
        mvn "$@"
    fi
}

echo `git log -n 1 --pretty=format:"%H"` > src/main/java/com/jeroensteenbeeke/topiroll/beholder/revision.txt
maven clean package -U
docker pull registry.jeroensteenbeeke.nl/hyperion-jetty:latest
docker build --no-cache -t beholder:latest .
docker save beholder:latest > beholder-docker.tar.gz
