package com.banno.twitterkafka

import twitter4j.{Status, StatusListener}
import com.twitter.finagle.Service

trait DruidStatusListener extends StatusListener {
  def tweetDruidService: TweetDruidService
  def hashtagAggregateDruidService: HashtagAggregateDruidService

  val tweetBuffer = scala.collection.mutable.ListBuffer.empty[Tweet]
  val hashtagAggregateBuffer = scala.collection.mutable.HashMap.empty[String, HashtagAggregate]
  val maxTweetBufferSize = 60
  val maxHashtagAggregateBufferSize = 60

  override def onStatus(status: Status): Unit = {
    tweetBuffer += Tweet.fromStatus(status)

    HashtagAggregate.fromStatus(status).foreach { ha =>
      hashtagAggregateBuffer.update(ha.hashtag, hashtagAggregateBuffer.getOrElse(ha.hashtag, ha))
    }

    maybeSendToDruid()
  }

  private[this] def maybeSendToDruid(): Unit = {
    if (tweetBuffer.size >= maxTweetBufferSize) {
      println(s"Sending ${tweetBuffer.size} tweets to Druid...")
      val t1 = System.nanoTime
      tweetDruidService.beam(tweetBuffer)
      val t2 = System.nanoTime
      println(s"Sent ${tweetBuffer.size} tweets to Druid in ${(t2-t1)/1e6} msec")
      tweetBuffer.clear()
    }

    if (hashtagAggregateBuffer.size >= maxHashtagAggregateBufferSize) {
      val aggregates = hashtagAggregateBuffer.map(_._2).toList
      println(s"Sending ${aggregates.size} hashtag-aggregates to Druid...")
      val t1 = System.nanoTime
      hashtagAggregateDruidService.beam(aggregates)
      val t2 = System.nanoTime
      println(s"Sent ${aggregates.size} hashtag-aggregates to Druid in ${(t2-t1)/1e6} msec")
      hashtagAggregateBuffer.clear()
    }
  }

  override def onDeletionNotice(x$1: twitter4j.StatusDeletionNotice): Unit = {}
  override def onScrubGeo(x$1: Long,x$2: Long): Unit = {}
  override def onStallWarning(x$1: twitter4j.StallWarning): Unit = {}
  override def onTrackLimitationNotice(x$1: Int): Unit = {}
  override def onException(x$1: Exception): Unit = {}
}
