#!/bin/bash

echo "******CREATING DRUID DATABASE AND USER******"

echo "starting postgres"
gosu postgres pg_ctl -w start

echo "bootstrapping the postgres db"
gosu postgres psql -h localhost -p 5432 -U postgres -a -f /docker-entrypoint-initdb.d/druid-setup.sql

echo "stopping postgres"
gosu postgres pg_ctl stop
echo "stopped postgres"

echo ""
echo "******DRUID DATABASE AND USER CREATED******"
