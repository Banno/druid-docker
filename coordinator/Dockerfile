FROM registry.banno-internal.com/druid-base:0.7.1.1-hadoop-2.0.0-mr1-cdh4.4.0

MAINTAINER Zach Cox zach.cox@banno.com

RUN /opt/druid/bin/pull-deps.sh '["io.druid.extensions:postgresql-metadata-storage"]'

ENTRYPOINT exec /opt/druid/bin/start-node.sh coordinator
