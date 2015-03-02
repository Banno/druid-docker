FROM banno/druid-base:latest

MAINTAINER Zach Cox zach.cox@banno.com

ENTRYPOINT exec java -server -Xmx1g -Duser.timezone=UTC -Dfile.encoding=UTF-8 -Ddruid.host=$DRUID_HOST -Ddruid.extensions.coordinates=[\"io.druid.extensions:postgresql-metadata-storage\"] -Ddruid.extensions.localRepository=/usr/local/druid/repository -Ddruid.metadata.storage.type=postgresql -Ddruid.metadata.storage.connector.connectURI=jdbc:postgresql://$POSTGRES_HOST:5432/$POSTGRES_DATABASE -Ddruid.metadata.storage.connector.user=$POSTGRES_USER -Ddruid.metadata.storage.connector.password=$POSTGRES_PASSWORD -Ddruid.zk.service.host=$DRUID_ZK_SERVICE_HOST -Ddruid.coordinator.startDelay=PT5S -cp "/usr/local/druid/lib/*" io.druid.cli.Main server coordinator
