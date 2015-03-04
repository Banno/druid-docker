FROM dockerfile/java:oracle-java7

MAINTAINER Zach Cox zach.cox@banno.com

# Druid (release tarball)
ENV DRUID_VERSION 0.7.0
RUN wget -q -O - http://static.druid.io/artifacts/releases/druid-$DRUID_VERSION-bin.tar.gz | tar -xzf - -C /usr/local
RUN ln -s /usr/local/druid-$DRUID_VERSION /usr/local/druid

ADD common.runtime.properties /usr/local/druid/config/_common/

# pull dependencies for Druid extensions - uses druid.extensions.coordinates in common.runtime.properties, so any time that file changes this will re-pulll the deps, which is annoying
# this could probably be resolved by specifying -Ddruid.extensions.coordinates directly in this RUN command and doing this before the ADD... 
RUN java -cp "/usr/local/druid/config/_common:/usr/local/druid/lib/*" io.druid.cli.Main tools pull-deps

ENV DRUID_COMMON_JAVA_OPTIONS -server -Duser.timezone=UTC -Dfile.encoding=UTF-8
