#!/bin/sh

set -e

if [ "$1" = jetty.sh ]; then
	if ! command -v bash >/dev/null 2>&1 ; then
		cat >&2 <<- 'EOWARN'
			********************************************************************
			ERROR: bash not found. Use of jetty.sh requires bash.
			********************************************************************
		EOWARN
		exit 1
	fi
	cat >&2 <<- 'EOWARN'
		********************************************************************
		WARNING: Use of jetty.sh from this image is deprecated and may
			 be removed at some point in the future.

			 See the documentation for guidance on extending this image:
			 https://github.com/docker-library/docs/tree/master/jetty
		********************************************************************
	EOWARN
fi

JETTY_CONFIG=$JETTY_HOME/etc/jetty.xml

sed -i "s/__BACKEND__/$DOCKER_CFG_BACKEND_URL/" $JETTY_CONFIG

if ! command -v -- "$1" >/dev/null 2>&1 ; then
	set -- java -jar "-Djava.io.tmpdir=$TMPDIR" "$JETTY_HOME/start.jar" "$@"
fi

exec "$@"
