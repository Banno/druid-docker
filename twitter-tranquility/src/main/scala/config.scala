package com.banno.twitterkafka

import com.typesafe.config.ConfigFactory
import twitter4j.conf.ConfigurationBuilder
import java.util.Properties
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry
import com.metamx.tranquility.druid.{DruidBeams, DruidLocation, DruidRollup, SpecificDruidDimensions, DruidTuning}
import com.metamx.tranquility.beam.ClusteredBeamTuning
import com.metamx.common.Granularity
import io.druid.query.aggregation.{CountAggregatorFactory, LongSumAggregatorFactory}
import io.druid.query.aggregation.hyperloglog.HyperUniquesAggregatorFactory
import io.druid.granularity.QueryGranularity
import io.druid.data.input.impl.TimestampSpec
import org.joda.time.Period

trait Config {
  lazy val config = ConfigFactory.load()
}

trait TwitterConfig extends Config {
  lazy val oauthConsumerKey = config.getString("twitter.oauth.consumer-key")
  lazy val oauthConsumerSecret = config.getString("twitter.oauth.consumer-secret")
  lazy val oauthAccessToken = config.getString("twitter.oauth.access-token")
  lazy val oauthAccessTokenSecret = config.getString("twitter.oauth.access-token-secret")
  lazy val twitter4jConfiguration = new ConfigurationBuilder()
    .setOAuthConsumerKey(oauthConsumerKey)
    .setOAuthConsumerSecret(oauthConsumerSecret)
    .setOAuthAccessToken(oauthAccessToken)
    .setOAuthAccessTokenSecret(oauthAccessTokenSecret)
    .build()
}

trait DruidConfig extends Config {
  val indexService = "overlord" // Your overlord's druid.service, with slashes replaced by colons.
  val firehosePattern = "druid:firehose:%s" // Make up a service pattern, include %s somewhere in it.
  val discoveryPath = "/druid/discovery" // Your overlord's druid.discovery.curator.path.
  val dataSource = "tweets"
  val dimensions = IndexedSeq(
    "language",
    "utcOffset",
    "hashtags",
    "urls",
    "userId") //TODO spatialDimensions for latitude and longitude
  val aggregators = Seq(
    new CountAggregatorFactory("count"), 
    new LongSumAggregatorFactory("total_retweet_count", "retweetCount"),
    new HyperUniquesAggregatorFactory("user_id_hll", "userId"),
    new HyperUniquesAggregatorFactory("hashtags_hll", "hashtags"),
    new HyperUniquesAggregatorFactory("urls_hll", "urls"))

  // Tranquility needs to be able to extract timestamps from your object type (in this case, Map<String, Object>).
  val timestamper = (tweet: Tweet) => tweet.timestamp

  val timestampSpec = new TimestampSpec("timestamp", "auto")

  val curator = CuratorFrameworkFactory
    .builder()
    .connectString("192.168.59.103:2181") //TODO config
    .retryPolicy(new ExponentialBackoffRetry(1000, 20, 30000))
    .build();
  curator.start()

  // Tranquility needs to be able to serialize your object type. By default this is done with Jackson. If you want to
  // provide an alternate serializer, you can provide your own via ```.objectWriter(...)```. In this case, we won't
  // provide one, so we're just using Jackson:
  val druidService = DruidBeams
    .builder(timestamper)
    .timestampSpec(timestampSpec)
    .curator(curator)
    .discoveryPath(discoveryPath)
    .location(DruidLocation(indexService, firehosePattern, dataSource))
    .rollup(DruidRollup(SpecificDruidDimensions(dimensions), aggregators, QueryGranularity.NONE))
    .tuning(
      ClusteredBeamTuning(
        segmentGranularity = Granularity.MINUTE,
        windowPeriod = new Period("PT10S"),
        partitions = 1,
        replicants = 1
      )
    )
    .druidTuning(new DruidTuning(75000, Period.seconds(10), 0))
    .buildService()
}
