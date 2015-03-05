package com.banno.twitterkafka

import twitter4j.TwitterStreamFactory

object Main 
    extends App 
    with SendTweetsToDruid
    with TwitterConfig
    with DruidConfig {
  val stream = new TwitterStreamFactory(twitter4jConfiguration).getInstance
  stream.addListener(this)
  stream.sample()
}
