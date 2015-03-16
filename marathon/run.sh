#!/bin/bash

JSON_FILE=$1
curl -X POST -H "Content-Type: application/json" -d @$JSON_FILE http://192.168.59.103:8080/v2/apps
