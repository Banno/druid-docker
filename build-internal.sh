#!/bin/bash

TAG="latest"

docker build -t registry.banno-internal.com/druid-base:$TAG base
docker build -t registry.banno-internal.com/druid-hadoop-base:$TAG hadoop-base
docker build -t registry.banno-internal.com/druid-broker:$TAG broker
docker build -t registry.banno-internal.com/druid-coordinator:$TAG coordinator
docker build -t registry.banno-internal.com/druid-historical:$TAG historical
docker build -t registry.banno-internal.com/druid-overlord:$TAG overlord
