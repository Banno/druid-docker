package com.banno.twitterkafka

import twitter4j.{Status, StatusListener}
import org.joda.time.DateTime
// import jsonz._
// import jsonz.DefaultFormats._
// import jsonz.joda.JodaTimeFormats._
import com.metamx.tranquility.typeclass.ObjectWriter

case class Tweet(
  userId: Long,
  text: String,
  timestamp: DateTime,
  language: Option[String],
  utcOffset: Option[Int],
  latitude: Option[Double],
  longitude: Option[Double],
  retweetCount: Int,
  favoriteCount: Int,
  hashtags: Seq[String],
  urls: Seq[String])

object Tweet {
  def fromStatus(status: Status): Tweet = Tweet(
    status.getUser.getId,
    status.getText,
    new DateTime(status.getCreatedAt.getTime),
    Option(status.getLang),
    Option(status.getUser.getUtcOffset),
    Option(status.getGeoLocation).flatMap(g => Option(g.getLatitude)),
    Option(status.getGeoLocation).flatMap(g => Option(g.getLongitude)),
    status.getRetweetCount,
    status.getFavoriteCount,
    status.getHashtagEntities.map(_.getText),
    status.getURLEntities.flatMap(url => Option(url.getDisplayURL)))

  // implicit val jsonFormat = productFormat11("userId", "text", "timestamp", "language", "utcOffset", "latitude", "longitude", "retweetCount", "favoriteCount", "hashtags", "urls")(Tweet.apply)(Tweet.unapply)
}

object TweetObjectWriter extends ObjectWriter[Tweet] {
  def asBytes(tweet: Tweet): Array[Byte] = ???//Jsonz.toJsonBytes(tweet)
  def batchAsBytes(tweets: TraversableOnce[Tweet]): Array[Byte] = ???//Jsonz.toJsonBytes(tweets.toSeq)
}
