#!/bin/bash

BROKER_HOST=mesosslave1.dev.banno.com
BROKER_PORT=31821
JSON_FILE=$1
curl -X POST "http://${BROKER_HOST}:${BROKER_PORT}/druid/v2/?pretty" -H 'content-type: application/json'  -d @$JSON_FILE
