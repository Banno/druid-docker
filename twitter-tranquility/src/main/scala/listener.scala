package com.banno.twitterkafka

import twitter4j.{Status, StatusListener}
import com.twitter.finagle.Service

trait SendTweetsToDruid extends StatusListener {
  val buffer = scala.collection.mutable.ListBuffer.empty[Tweet]
  val maxBufferSize = 60
  def druidService: Service[Seq[Tweet], Int]

  override def onStatus(status: Status): Unit = {
    buffer += Tweet.fromStatus(status)
    maybeSendTweetsToDruid()
  }

  def maybeSendTweetsToDruid(): Unit = {
    if (buffer.size >= maxBufferSize) {
      println(s"Sending ${buffer.size} tweets to Druid...")
      val t1 = System.nanoTime
      druidService(buffer)
      val t2 = System.nanoTime
      println(s"Sent ${buffer.size} tweets to Druid in ${(t2-t1)/1e6} msec")
      buffer.clear()
    }
  }

  override def onDeletionNotice(x$1: twitter4j.StatusDeletionNotice): Unit = {}
  override def onScrubGeo(x$1: Long,x$2: Long): Unit = {}
  override def onStallWarning(x$1: twitter4j.StallWarning): Unit = {}
  override def onTrackLimitationNotice(x$1: Int): Unit = {}
  override def onException(x$1: Exception): Unit = {}
}
