## Unsupported Project!

**NOTE:** we are no longer using Druid, and therefore we are no longer supporting this repository or any Docker images built from it. It only remains on github as a reference for others. If you'd like to take over this repo, please open an issue.

## Overview

Example of running Druid broker, coordinator, historical and overlord nodes in separate Docker containers, along with Zookeeper and Postgres in their own Docker containers, all wired together via fig.

An eventual goal of this project is to provide Docker images for all of the Druid node types as automated builds on https://hub.docker.com.

NOTE: this is very much a work-in-progress and learning exercise.

![](http://0.media.collegehumor.cvcdn.com/30/69/5d86b2b73fe03210cba0725aafbe608c.gif)

## Quickstart

```
./build.sh
fig up -d druid
cd random-tranquility
sbt run
```

All Druid nodes (broker, coordinator, historical and overlord) should start up pretty quickly. Check the logs of any node using `fig logs [nodeType]`, e.g. `fig logs druidcoordinator1` or `fig logs druidoverlord1`.

Once the coordinator is up, its web console should be accesible at http://192.168.59.103:8081.

Once the overlord is up, its web console should be accessible at http://192.168.59.103:8085/console.html.

Send queries to the broker at http://192.168.59.103:8082/druid/v2/. Some example queries are provided, e.g.:

```
cd query
./query.sh random-counts.json
```

To shut everything down: `fig kill && fig rm --force`

## Hadoop Batch Indexing

```
./build.sh
fig up -d druid
http://hadoop.dev.banno.com:50070 and wait for safe mode to be turned off, takes about 30 secs
execute stuff in druid-pageviews/hdfs-commands to get data file into hdfs
./ingest.sh task5-hadoop.json <========== exec this in druid-pageviews to start hadoop index task
http://192.168.59.103:8085/console.html to make sure task is running, and check its logs
http://hadoop.dev.banno.com:8088 to check druid mapreduce job in yarn ui
```

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

druid-base image:
  - download & install released version of Druid
  - delete all .properties files
  - start-druid-node.sh 
    - meant to be used as the ENTRYPOINT in each node type image
    - reusable shell script that can start any druid node type
    - parameterized so specific node types can reuse it
    - replaces/appends env vars named `druid.x` into common.runtime.properties
    - try to use docker link env vars to wire things together? e.g. zookeeper, postgres
    - use exec to run the druid node, so signals (e.g. SIGTERM) get sent directly to druid jvm process
    - `DRUID_JAVA_OPTIONS` env var
  - pull-deps.sh
    - runs `java ... io.druid.cli.Main tools pull-deps` 
    - downloads extension dependencies to local dir
    - meant to be run during image building (i.e. in a RUN instruction)
    - if extension deps are not included in image, they will be downloaded when container is run (which takes forever)
    - needs to be parameterized to download any possible extension deps

druid-[nodeType] images:
  - RUN /druid/pull-deps.sh [extensions]
    - downloads extension deps that this node type needs into local dir, cached in image
  - ENTRYPOINT /druid/start-node.sh [nodeType]
    - puts env vars into common.runtime.properties (or into /druid/config/[nodeType]/runtime.properties?)
    - runs `exec java ... [nodeType]`
  - EXPOSE?

fig.yml:
  - define a service for each node type, using that node type's docker image
  - use links to run dependent services
  - use environment to set env vars (e.g. host, port, zookeeper)
  - use ports to expose the proper ports


### Druid Configuration

Druid will load files named `common.runtime.properties` and `runtime.properties` that it finds on the classpath. There is also a config `druid.properties.file` that Druid might use
if it can't find one of those files, not really sure though...

It is not possible to use environment variables in Druid's .properties files.

Druid does allow system properties to override configs in the .properties files, so you can do something like `java ... -Dsome.druid.config=$SOME_ENV_VAR ...`. One downside of doing this is then
`SOME_ENV_VAR` must always be set, and cannot be optional.

One approach to using env vars in .properties files is to have a "sidecar" shell script as the Docker image's ENTRYPOINT, which takes all env vars named `DRUID_X_Y`, convert them into strings like `x.y=value` and then replace/append those into the .properties file using e.g. sed. That script would then run the Druid java command. Care must be taken with this approach so that any signals (e.g. SIGTERM) get sent to the java process and not to the shell script (probably need to use `exec`).

Another option is to create generic base images using the stock .properties files, and then create sub-images with the actual .properties files to use overwriting the stock ones. A downside of this approach is that all config values are hard-coded and cannot be dynamic (e.g. random port chosen by Marathon). This also leads to creating separate Docker images for different environments, e.g. staging and production.

### Mesos

Lowest barrier-to-entry for running Druid on Mesos is probably to tell Marathon to run all of the nodes
  - write some marathon json, make a couple http posts, that's about it
  - there are definitely advantages to writing a mesos framework long-term, but for now we just need druid on mesos
  - if there are must-haves that marathon can't provide, then it will be good to figure those out now

Each Druid node type has its own Docker image, so will need its own Marathon application
  - no way to have 1 marathon application run separate druid node types
  - this is something a custom mesos framework could do
  - can still scale each node type independently
  - marathon directory, with 1 json file per node type

Do we need to constrain where marathon runs the node types?
  - Any requirements to run different node types on different machines?
  - Any requirements to run instances of same node type on different machines?
    - Guessing we probably want instances of same node type on different machines, for availability
    - e.g. don't run two historical nodes on same machine in case it fails
    - e.g. don't run two broker nodes on same machine in case it fails
  - How would these machine-uniqueness constraints be implemented via marathon?
    - https://mesosphere.github.io/marathon/docs/constraints.html
    - hostname:UNIQUE runs instance of same node type on different machines
    - hostname:LIKE:[regex] runs instance of same node type only on machines with hostnames that match the regex (e.g. hostname:LIKE:mesos-slave[1-3])
    - [attribute]:LIKE:[regex] if we tag our mesos slaves with attributes we can tell marathon to only run instances of node type on those machines

Can we use standard Druid port numbers? Or do we need to use Marathon-assigned random ports?
  - If there is only ever 1 instance of a given node type on a machine, then can probably use standard ports
  - If we do need to use dynamic ports, Druid has its own internal service discovery via Marathon for everything to find everything else
  - Could probably also use that externally if needed (e.g. to locate a broker node to query)
  - Should probably LB multiple brokers anyways for availability
  - Tranquility does not even need to know host:port of overlord, just zookeeper
  - Can probably start with standard ports if we're enforcing max 1 instance of each node type per machine, and support dynamic ports later if needed
  - Druid base image start-node.sh can now fall back to druid.host=$HOST and druid.port=$PORT

What about other config?
  - e.g. postgres metadata connection
  - e.g. buffer sizes
  - since the druid docker containers now allow env vars like `druid_x_y_z` to override any configs like `druid.x.y.z` we can just use env vars in marathon json

Observations running 2 of each node type:
  - Can query either broker node
    - Should be able to round-robin load balance multiple brokers using haproxy
  - One overlord is the leader
  - One coordinator is the leader

Definitely need to test failover:
  - If one overlord dies, does the other one take over tasks?
    - What's the difference between overlord and peon task dying?
  - If one coordinator dies, does the other one take over coordination?
  - If one historical dies, do other historicals take over its segments?

Special considerations when running on Mesos locally in boot2docker:
  - With more than one Mesos slave in boot2docker, and more than one of a druid node type, have to use random ports, otherwise e.g. broker on each mesos slave will try to bind to port 8082, all but one will fail
  - Random ports makes querying brokers more painful, as you have to look up the host & port to use
  - Need to add something like "127.0.0.1 mesosslave1.dev.banno.com mesosslave2.dev.banno.com" to boot2docker's /etc/hosts, so that overlord & peons can communicate
