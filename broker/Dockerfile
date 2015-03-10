FROM banno/druid-base:latest

MAINTAINER Zach Cox zach.cox@banno.com

#need to use exec so the java process from start-node.sh becomes PID 1, or maybe we could use the exec format? i.e. ["/opt/druid/bin/start-node.sh", "broker"]
ENTRYPOINT exec /opt/druid/bin/start-node.sh broker
