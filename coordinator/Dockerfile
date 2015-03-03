FROM banno/druid-base:latest

MAINTAINER Zach Cox zach.cox@banno.com

ENTRYPOINT exec java -server -Xmx1g -Duser.timezone=UTC -Dfile.encoding=UTF-8 -Ddruid.coordinator.startDelay=PT5S -cp "/usr/local/druid/config/_common:/usr/local/druid/lib/*" io.druid.cli.Main server coordinator
