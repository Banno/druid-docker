#!/bin/bash

TAG="0.7.1.1-hadoop-2.0.0-mr1-cdh4.4.0"

docker push registry.banno-internal.com/druid-base:$TAG
docker push registry.banno-internal.com/druid-hadoop-base:$TAG
docker push registry.banno-internal.com/druid-broker:$TAG
docker push registry.banno-internal.com/druid-coordinator:$TAG
docker push registry.banno-internal.com/druid-historical:$TAG
docker push registry.banno-internal.com/druid-realtime:$TAG
docker push registry.banno-internal.com/druid-overlord:$TAG
