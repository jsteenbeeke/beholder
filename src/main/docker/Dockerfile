FROM jetty:latest
# MAINTAINER 

COPY beholder-web.war $JETTY_BASE/webapps/beholder-web.war
COPY initExt.sh /root/initExt.sh
COPY jetty.jndi /root/jetty.jndi
COPY insert.sh /root/insert.sh
COPY docker-entrypoint.sh docker-entrypoint.sh
RUN /bin/bash /root/initExt.sh
RUN /bin/bash /root/insert.sh \</Configure\> /root/jetty.jndi $JETTY_HOME/etc/jetty.xml
ENTRYPOINT ["/docker-entrypoint.sh"]
