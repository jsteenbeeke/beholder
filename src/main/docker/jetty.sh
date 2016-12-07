#!/bin/sh

set -e

echo Testing presence of environment variables

if [ -z "$DOCKER_CFG_USERNAME" ]; then
	echo "DOCKER_CFG_USERNAME is not set"
	exit 1
fi

if [ -z "$DOCKER_CFG_PASSWORD" ]; then
	echo "DOCKER_CFG_PASSWORD is not set"
	exit 1
fi

if [ -z "$DOCKER_CFG_HOST" ]; then
	echo "DOCKER_CFG_HOST is not set"
	exit 1
fi

if [ -z "$DOCKER_CFG_DATABASE" ]; then
	echo "DOCKER_CFG_DATABASE is not set"
	exit 1
fi

# Sanitize vars
DOCKER_CFG_DATABASE=$(echo $DOCKER_CFG_DATABASE|tr -d '\n')
DOCKER_CFG_HOST=$(echo $DOCKER_CFG_HOST|tr -d '\n')
DOCKER_CFG_PASSWORD=$(echo $DOCKER_CFG_PASSWORD|tr -d '\n')
DOCKER_CFG_USERNAME=$(echo $DOCKER_CFG_USERNAME|tr -d '\n')

echo =========================================
echo URL     : $DOCKER_CFG_HOST
echo USERNAME: $DOCKER_CFG_USERNAME
echo DATABASE: $DOCKER_CFG_DATABASE
echo =========================================
# echo Testing database connection
# PGPASSWORD=$DOCKER_CFG_PASSWORD psql -h $DOCKER_CFG_HOST -U $DOCKER_CFG_USERNAME $DOCKER_CFG_DATABASE -w -c "\dt"

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

sed -i "s/__USERNAME__/$DOCKER_CFG_USERNAME/" $JETTY_CONFIG
sed -i "s/__PASSWORD__/$DOCKER_CFG_PASSWORD/" $JETTY_CONFIG
sed -i "s/__HOST__/$DOCKER_CFG_HOST/" $JETTY_CONFIG
sed -i "s/__DATABASE__/$DOCKER_CFG_DATABASE/" $JETTY_CONFIG

if ! command -v -- "$1" >/dev/null 2>&1 ; then
	set -- java -jar -Djava.net.preferIPv4Stack=true -Dwicket.configuration=deployment "-Djava.io.tmpdir=$TMPDIR" "$JETTY_HOME/start.jar" "$@"
fi

exec "$@"
