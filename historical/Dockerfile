FROM banno/druid-base:latest

MAINTAINER Zach Cox zach.cox@banno.com

RUN /opt/druid/bin/pull-deps.sh '["io.druid.extensions:druid-hdfs-storage"]'

ENTRYPOINT exec /opt/druid/bin/start-node.sh historical
