#!/bin/bash

JSON_FILE=$1
curl -X POST 'http://192.168.59.103:8082/druid/v2/?pretty' -H 'content-type: application/json'  -d @$JSON_FILE
