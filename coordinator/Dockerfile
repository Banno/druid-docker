FROM banno/druid-base:latest

MAINTAINER Zach Cox zach.cox@banno.com

RUN /opt/druid/bin/pull-deps.sh '["io.druid.extensions:postgresql-metadata-storage"]'

ENTRYPOINT exec /opt/druid/bin/start-node.sh coordinator
