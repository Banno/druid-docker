FROM library/java:openjdk-8-jdk

MAINTAINER Zach Cox zach.cox@banno.com

ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64

ENV DRUID_VERSION 0.7.1.1-hadoop-2.0.0-mr1-cdh4.4.0
#RUN wget -q -O - http://static.druid.io/artifacts/releases/druid-$DRUID_VERSION-bin.tar.gz | tar -xzf - -C /opt
ADD druid-$DRUID_VERSION-bin.tar.gz /opt/
RUN ln -s /opt/druid-$DRUID_VERSION /opt/druid

ADD common.runtime.properties /opt/druid/config/_common/
ADD start-node.sh pull-deps.sh /opt/druid/bin/
