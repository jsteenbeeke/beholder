FROM registry.jeroensteenbeeke.nl/hyperion-jetty:10-jdk20
MAINTAINER Jeroen Steenbeeke,j.steenbeeke@gmail.com

COPY target/beholder.war $JETTY_BASE/webapps/beholder.war
COPY target/classes/hyperion-log.xml $JETTY_BASE/hyperion-log.xml

