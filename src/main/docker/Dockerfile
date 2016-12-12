FROM jetty:alpine
MAINTAINER Jeroen Steenbeeke,j.steenbeeke@gmail.com

COPY beholder.war $JETTY_BASE/webapps/root.war
COPY jetty.forward /root/jetty.forward
COPY xmlinsert.jar /root/
RUN mkdir -p /home/jetty
RUN chown jetty:jetty /home/jetty
RUN java -jar /root/xmlinsert.jar New /root/jetty.forward $JETTY_HOME/etc/jetty.xml id=httpConfig
RUN mkdir $JETTY_HOME/.hyperion/
EXPOSE 8080
VOLUME ["/home/jetty/.hyperion/"]
ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["java","-Djava.io.tmpdir=/tmp/jetty","-Dwicket.configuration=deployment","-jar","/usr/local/jetty/start.jar"]
