FROM registry.jeroensteenbeeke.nl/hyperion-jetty:latest
MAINTAINER Jeroen Steenbeeke,j.steenbeeke@gmail.com

COPY target/beholder.war $JETTY_BASE/webapps/root.war
