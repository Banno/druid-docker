Consumes from the Twitter Streaming API, converts each tweet into a flat JSON object that Druid can consume, and sends each tweet to a Kafka topic. This is meant to work 
with https://github.com/Banno/druid-docker.

This example code was originally meant to be made public outside Banno, so it was built without using any internal Banno dependencies.

If this repository is ever made public, the src/main/resources/twitter-oauth.conf file should *not* be included!
