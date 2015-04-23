FROM dockerfile/java:oracle-java7

MAINTAINER Zach Cox zach.cox@banno.com

ENV DRUID_VERSION 0.7.1.1
RUN wget -q -O - http://static.druid.io/artifacts/releases/druid-$DRUID_VERSION-bin.tar.gz | tar -xzf - -C /opt
#ADD druid-$DRUID_VERSION-bin.tar.gz /opt/
RUN ln -s /opt/druid-$DRUID_VERSION /opt/druid

ADD common.runtime.properties /opt/druid/config/_common/
ADD start-node.sh pull-deps.sh /opt/druid/bin/
