FROM registry.jeroensteenbeeke.nl/hyperion-jetty:10-jdk17
MAINTAINER Jeroen Steenbeeke,j.steenbeeke@gmail.com

COPY target/beholder.war $JETTY_BASE/webapps/root.war
COPY target/classes/hyperion-log.xml $JETTY_BASE/hyperion-log.xml

