## Overview

Example of running Druid broker, coordinator, historical and realtime nodes in separate Docker containers, along with Zookeeper and Postgres in their own Docker containers, all wired together via fig.

An eventual goal of this project is to provide Docker images for all of the Druid node types as automated builds on https://hub.docker.com.

NOTE: this is very much a work-in-progress and learning exercise.

![](http://0.media.collegehumor.cvcdn.com/30/69/5d86b2b73fe03210cba0725aafbe608c.gif)

## Quickstart

```
./build.sh
fig up -d druid
```

The broker and historical nodes start up immediately, but the coordinator and realtime nodes take awhile due to downloading extension dependencies on startup (TODO pre-cache these in docker images). Check the status of those nodes using `fig logs druidcoordinator` and `fig logs druidrealtime`.

Once the coordinator is up, its web console should be accesible at http://192.168.59.103:8081.

Send queries to the broker at http://192.168.59.103:8082/druid/v2/ (TODO example query)

To shut everything down: `fig kill && fig rm --force`

## Design Notes

Druid [node types](http://druid.io/docs/0.7.0/Design.html):

  - [Historical](http://druid.io/docs/0.7.0/Historical.html)
    - Serves queries for "old" data
  - [Realtime](http://druid.io/docs/0.7.0/Realtime.html)
    - Serves queries for "new" data
  - [Coordinator](http://druid.io/docs/0.7.0/Coordinator.html)
  - [Broker](http://druid.io/docs/0.7.0/Broker.html)
    - Clients send queries to Broker 
  - [Indexer](http://druid.io/docs/0.7.0/Indexing-Service.html)
    - Overlord
    - Middle manager
    - Peon
    - Sounds like Indexer nodes are optional, and you can ingest using Realtime nodes directly?
    - Without using Indexer, the Realtime nodes are not HA?
    - If using Indexer then you don't need Realtime nodes?

Each node is launched in basically the same way. An argument of `nodeType` determines which type of node it will be:

```
java ... io.druid.cli.Main server [nodeType]
```

There should probably one base Docker image containing a Druid "stem cell", and then a separate Docker image for each specific node type. Each Druid Docker container should only 
run a single Druid node process and nothing else. Entrypoint should run the node's `java` command. Configuration needs to be customizable via environment variables. 
May also make sense to support other ways of using custom configuration files (instead of only env vars) such as sub-images, shared volumes, etc. Druid does not seem to support 
config values in property files being overriden by env vars, so it might be OK to support a few env var overrides (i.e. by doing `-Dsome.druid.property=$SOME_ENV_VAR`) but 
heavy customization should probably be done in property files added to sub-images.

External dependencies such as Zookeeper, metadata storage (e.g. Postgres) and deep storage (e.g. HDFS) should be fully external; these
should absolutely not be running in any Druid Docker container. Setting up a "distributed" Druid cluster should be coordinated via a fig.yml.

The Docker images created in this repository should be completely generic and non-Banno-specific. We can add our own sidecars around these Docker images separately, in a private repository.

Docker images:
  - base
  - historical
  - realtime
  - coordinator
  - broker
  - overlord
  - middle-manager

The "simpler" setup uses Realtime nodes instead of Indexers (overlord and middle-manager):
  - historical
  - realtime
  - coordinator
  - broker

The more complex option uses Indexers instead of Realtime nodes:
  - historical
  - overlord
  - middle-manager
  - coordinator
  - broker
  - tranquility?
