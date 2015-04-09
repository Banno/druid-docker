#!/bin/bash

NODE_TYPE=$1

export druid_host=${druid_host:-$HOST} #Marathon sets $HOST to the Mesos slave machine's hostname
export druid_port=${druid_port:-$PORT} #Marathon sets $PORT to a random port number

#TODO try to get zookeeper host/port from link env vars?

#TODO try to get postgres host/port from link env vars?

export DRUID_PROPERTIES_FILE=/opt/druid/config/_common/common.runtime.properties

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

export DRUID_EXTRA_CLASSPATH=${DRUID_EXTRA_CLASSPATH:-}

#dumping all the env vars and final config file helps debugging
env | sort
cat $DRUID_PROPERTIES_FILE

#exec should make the java process PID 1 so that any signals sent to this container go to that process
echo java -server -Duser.timezone=UTC -Dfile.encoding=UTF-8 $DRUID_JAVA_OPTIONS -cp "/opt/druid/config/_common:/opt/druid/lib/*${DRUID_EXTRA_CLASSPATH}" io.druid.cli.Main server $NODE_TYPE
exec java -server -Duser.timezone=UTC -Dfile.encoding=UTF-8 $DRUID_JAVA_OPTIONS -cp "/opt/druid/config/_common:/opt/druid/lib/*${DRUID_EXTRA_CLASSPATH}" io.druid.cli.Main server $NODE_TYPE
