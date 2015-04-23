FROM banno/druid-hadoop-base:latest

MAINTAINER Zach Cox zach.cox@banno.com

RUN /opt/druid/bin/pull-deps.sh '["io.druid.extensions:postgresql-metadata-storage", "io.druid.extensions:druid-hdfs-storage"]'

ENTRYPOINT exec /opt/druid/bin/start-node.sh overlord
