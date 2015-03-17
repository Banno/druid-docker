#!/bin/bash

DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
$DIR/run.sh $DIR/broker.json
$DIR/run.sh $DIR/coordinator.json
$DIR/run.sh $DIR/historical.json
$DIR/run.sh $DIR/overlord.json
