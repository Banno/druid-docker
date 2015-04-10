#!/bin/bash

sed s/HOSTNAME/$HOSTNAME/ /usr/local/hadoop/etc/hadoop/mapred-site.xml.template > /usr/local/hadoop/etc/hadoop/mapred-site.xml

/etc/bootstrap.sh -d
