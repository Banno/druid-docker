#!/bin/bash

NODE_TYPE=$1

#TODO try to get zookeeper host/port from link env vars?

#TODO try to get postgres host/port from link env vars?

DRUID_PROPERTIES_FILE=/opt/druid/config/_common/common.runtime.properties

#put env vars in common.runtime.properties, they will be named like druid_x_y_z and we need to rename to druid.x.y.z
for var in `env` #$var will contain something like: druid_host=192.168.59.103
do
  if [[ "$var" =~ ^druid_ ]]; then
    env_var=`echo "$var" | sed -r "s/(.*)=.*/\1/g"` #extract just the env var name out of $var
    druid_property=`echo "$env_var" | tr _ .`
    if egrep -q "(^|^#)$druid_property" $DRUID_PROPERTIES_FILE; then
        sed -r -i "s@(^|^#)($druid_property)=(.*)@\2=${!env_var}@g" $DRUID_PROPERTIES_FILE #note that no config values may contain an '@' char
    else
        echo "$druid_property=${!env_var}" >> $DRUID_PROPERTIES_FILE
    fi
  fi
done

#dumping all the env vars and final config file helps debugging
env | sort
cat $DRUID_PROPERTIES_FILE

#exec should make the java process PID 1 so that any signals sent to this container go to that process
exec java -server -Duser.timezone=UTC -Dfile.encoding=UTF-8 $DRUID_JAVA_OPTIONS -cp "/opt/druid/config/_common:/opt/druid/lib/*" io.druid.cli.Main server $NODE_TYPE
