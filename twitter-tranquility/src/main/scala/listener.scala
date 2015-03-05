package com.banno.twitterkafka

import twitter4j.{Status, StatusListener}
import com.twitter.finagle.Service

trait SendTweetsToDruid extends StatusListener {
  def druidService: Service[Seq[Tweet], Int]

  override def onStatus(status: Status): Unit = {
    druidService(Seq(Tweet.fromStatus(status)))
  }

  override def onDeletionNotice(x$1: twitter4j.StatusDeletionNotice): Unit = {}
  override def onScrubGeo(x$1: Long,x$2: Long): Unit = {}
  override def onStallWarning(x$1: twitter4j.StallWarning): Unit = {}
  override def onTrackLimitationNotice(x$1: Int): Unit = {}
  override def onException(x$1: Exception): Unit = {}
}
