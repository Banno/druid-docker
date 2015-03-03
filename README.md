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

All Druid nodes (broker, coordinator, historical and realtime) should start up pretty quickly. Check the logs of any node using `fig logs [nodeType]`, e.g. `fig logs druidcoordinator` or `fig logs druidrealtime`.

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

### Druid Configuration

Druid will load files named `common.runtime.properties` and `runtime.properties` that it finds on the classpath. There is also a config `druid.properties.file` that Druid might use
if it can't find one of those files, not really sure though...

It is not possible to use environment variables in Druid's .properties files.

Druid does allow system properties to override configs in the .properties files, so you can do something like `java ... -Dsome.druid.config=$SOME_ENV_VAR ...`. One downside of doing this is then
`SOME_ENV_VAR` must always be set, and cannot be optional.

One approach to using env vars in .properties files is to have a "sidecar" shell script as the Docker image's ENTRYPOINT, which takes all env vars named `DRUID_X_Y`, convert them into strings like `x.y=value` and then replace/append those into the .properties file using e.g. sed. That script would then run the Druid java command. Care must be taken with this approach so that any signals (e.g. SIGTERM) get sent to the java process and not to the shell script (probably need to use `exec`).

Another option is to create generic base images using the stock .properties files, and then create sub-images with the actual .properties files to use overwriting the stock ones. A downside of this approach is that all config values are hard-coded and cannot be dynamic (e.g. random port chosen by Marathon). This also leads to creating separate Docker images for different environments, e.g. staging and production.
