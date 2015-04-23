#!/bin/bash

EXTENSIONS=$1
DRUID_PROPERTIES_FILE=/opt/druid/config/_common/common.runtime.properties
echo "druid.extensions.coordinates=$EXTENSIONS" >> $DRUID_PROPERTIES_FILE
echo "druid.extensions.localRepository=/opt/druid/repository" >> $DRUID_PROPERTIES_FILE
#echo 'druid.extensions.remoteRepositories=["http://192.168.59.3:9090", "http://repo1.maven.org/maven2/", "https://metamx.artifactoryonline.com/metamx/pub-libs-releases-local"]' >> $DRUID_PROPERTIES_FILE

java -cp "/opt/druid/config/_common:/opt/druid/lib/*" io.druid.cli.Main tools pull-deps
