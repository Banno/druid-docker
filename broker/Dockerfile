FROM banno/druid-base:latest

MAINTAINER Zach Cox zach.cox@banno.com

ENTRYPOINT exec java -server -Xmx1g -Duser.timezone=UTC -Dfile.encoding=UTF-8 -Ddruid.host=$DRUID_HOST -Ddruid.computation.buffer.size=67108864 -Ddruid.broker.cache.sizeInBytes=33554432 -Ddruid.zk.service.host=$DRUID_ZK_SERVICE_HOST -cp "/usr/local/druid/lib/*" io.druid.cli.Main server broker
