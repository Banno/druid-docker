#!/bin/bash

TAG="0.7.1.1-hadoop-2.0.0-mr1-cdh4.4.0"

docker build -t registry.banno-internal.com/druid-base:$TAG base
docker build -t registry.banno-internal.com/druid-hadoop-base:$TAG hadoop-base
docker build -t registry.banno-internal.com/druid-broker:$TAG broker
docker build -t registry.banno-internal.com/druid-coordinator:$TAG coordinator
docker build -t registry.banno-internal.com/druid-historical:$TAG historical
docker build -t registry.banno-internal.com/druid-realtime:$TAG realtime
docker build -t registry.banno-internal.com/druid-overlord:$TAG overlord
