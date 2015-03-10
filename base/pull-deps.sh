#!/bin/bash

EXTENSIONS=$1
DRUID_PROPERTIES_FILE=/opt/druid/config/_common/common.runtime.properties
echo "druid.extensions.coordinates=$EXTENSIONS" >> $DRUID_PROPERTIES_FILE
echo "druid.extensions.localRepository=/opt/druid/repository" >> $DRUID_PROPERTIES_FILE

java -cp "/opt/druid/config/_common:/opt/druid/lib/*" io.druid.cli.Main tools pull-deps
