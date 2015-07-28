FROM registry.banno-internal.com/druid-base:0.7.1.1-hadoop-2.0.0-mr1-cdh4.4.0

MAINTAINER Zach Cox zach.cox@banno.com

#need to use exec so the java process from start-node.sh becomes PID 1, or maybe we could use the exec format? i.e. ["/opt/druid/bin/start-node.sh", "broker"]
ENTRYPOINT exec /opt/druid/bin/start-node.sh broker
