FROM registry.banno-internal.com/druid-hadoop-base:0.7.1.1-hadoop-2.0.0-mr1-cdh4.4.0

MAINTAINER Zach Cox zach.cox@banno.com

RUN /opt/druid/bin/pull-deps.sh '["io.druid.extensions:druid-hdfs-storage", "io.druid.extensions:druid-kafka-eight"]'

ENTRYPOINT exec /opt/druid/bin/start-node.sh realtime
