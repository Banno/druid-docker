#!/bin/bash

TAG="latest"

docker build -t banno/druid-base:$TAG base
docker build -t banno/druid-hadoop-base:$TAG hadoop-base
docker build -t banno/druid-broker:$TAG broker
docker build -t banno/druid-coordinator:$TAG coordinator
docker build -t banno/druid-historical:$TAG historical
# docker build -t banno/druid-middle-manager:$TAG middle-manager
docker build -t banno/druid-overlord:$TAG overlord
# docker build -t banno/druid-realtime:$TAG realtime
