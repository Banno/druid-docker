package com.banno.twitterkafka

import twitter4j.TwitterStreamFactory

object Main
    extends App
    with DruidStatusListener
    with TwitterConfig {
  val tweetDruidService = new TweetDruidService
  val hashtagAggregateDruidService = new HashtagAggregateDruidService
  val stream = new TwitterStreamFactory(twitter4jConfiguration).getInstance
  stream.addListener(this)
  stream.sample()
}
