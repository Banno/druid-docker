#!/bin/bash

TAG="latest"

docker push registry.banno-internal.com/druid-base:$TAG
docker push registry.banno-internal.com:7000/druid-hadoop-base:$TAG
docker push registry.banno-internal.com/druid-broker:$TAG
docker push registry.banno-internal.com/druid-coordinator:$TAG
docker push registry.banno-internal.com/druid-historical:$TAG
docker push registry.banno-internal.com:7000/druid-overlord:$TAG
