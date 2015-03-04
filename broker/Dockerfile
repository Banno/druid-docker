FROM banno/druid-base:latest

MAINTAINER Zach Cox zach.cox@banno.com

ENTRYPOINT exec java $DRUID_COMMON_JAVA_OPTIONS -Xmx1g -Ddruid.port=$DRUID_PORT -Ddruid.computation.buffer.size=67108864 -Ddruid.broker.cache.sizeInBytes=33554432 -cp "/usr/local/druid/config/_common:/usr/local/druid/lib/*" io.druid.cli.Main server broker
