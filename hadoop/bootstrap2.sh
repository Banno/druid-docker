#!/bin/bash

sed s/HOSTNAME/$HOSTNAME/ /usr/local/hadoop/etc/hadoop/core-site.xml.template > /usr/local/hadoop/etc/hadoop/core-site.xml

#CentOS seems to use Eastern time zone by default, change to UTC
rm /etc/localtime
ln -s /usr/share/zoneinfo/UTC /etc/localtime

exec /etc/bootstrap.sh -d
