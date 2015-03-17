#!/bin/bash

#when using random port chosen by marathon, need to check broker logs (or Zookeeper) to get the right host and port to use
BROKER_HOST=mesosslave1.dev.banno.com
BROKER_PORT=8082

JSON_FILE=$1
curl -X POST "http://${BROKER_HOST}:${BROKER_PORT}/druid/v2/?pretty" -H 'content-type: application/json'  -d @$JSON_FILE
