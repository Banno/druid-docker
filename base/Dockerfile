FROM dockerfile/java:oracle-java7

MAINTAINER Zach Cox zach.cox@banno.com

# Druid (release tarball)
ENV DRUID_VERSION 0.7.0
RUN wget -q -O - http://static.druid.io/artifacts/releases/druid-$DRUID_VERSION-bin.tar.gz | tar -xzf - -C /usr/local
RUN ln -s /usr/local/druid-$DRUID_VERSION /usr/local/druid

# pull dependencies for Druid extensions ???
