package com.banno

import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry
import com.metamx.tranquility.druid.{DruidBeams, DruidLocation, DruidRollup, SpecificDruidDimensions, DruidTuning, DruidBeamConfig}
import com.metamx.tranquility.beam.ClusteredBeamTuning
import com.metamx.common.Granularity
import io.druid.query.aggregation.{CountAggregatorFactory, LongSumAggregatorFactory}
import io.druid.query.aggregation.hyperloglog.HyperUniquesAggregatorFactory
import io.druid.granularity.QueryGranularity
import io.druid.data.input.impl.TimestampSpec
import org.joda.time.{DateTime, Period}
import org.scala_tools.time.Imports._
import scala.util.Random
import java.util.UUID

case class Event(
  eventId: String,
  category: String, 
  value: Int, 
  timestamp: DateTime)

object Event {
  def newEventId = UUID.randomUUID.toString
  val categories = Seq("C1", "C2", "C3")
  def randomCategory = categories(Random.nextInt(categories.size))
  def randomValue = Random.nextInt(100)
  def newRandomEvent = Event(newEventId, randomCategory, randomValue, DateTime.now)
}

case class DruidBeamConfigImpl(
  val firehoseGracePeriod: Period = 5.minutes,
  val firehoseQuietPeriod: Period = 1.minute,
  val firehoseRetryPeriod: Period = 1.minute,
  val firehoseChunkSize: Int = 1000,
  val indexRetryPeriod: Period = 1.minute) extends DruidBeamConfig

object Main extends App {
  val indexService = "overlord" // Your overlord's druid.service, with slashes replaced by colons.
  val firehosePattern = "druid:firehose:%s" // Make up a service pattern, include %s somewhere in it.
  val discoveryPath = "/druid/discovery" // Your overlord's druid.discovery.curator.path.
  val dataSource = "random"
  val dimensions = IndexedSeq(
    // "eventId", //if eventId is not included as a dimension then druid only counts 15-21 events/min; with eventId it counts 575-576 events/min
    "category")
  val aggregators = Seq(
    new CountAggregatorFactory("count"), 
    new LongSumAggregatorFactory("total_value", "value"))

  // Tranquility needs to be able to extract timestamps from your object type (in this case, Map<String, Object>).
  val timestamper = (event: Event) => event.timestamp

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
    .timestampSpec(new TimestampSpec("timestamp", "auto"))
    .curator(curator)
    .discoveryPath(discoveryPath)
    .location(DruidLocation(indexService, firehosePattern, dataSource))
    .rollup(DruidRollup(SpecificDruidDimensions(dimensions), aggregators, indexGranularity = QueryGranularity.MINUTE))
    .tuning(
      ClusteredBeamTuning(
        segmentGranularity = Granularity.HOUR,
        warmingPeriod = new Period("PT20S"),
        windowPeriod = new Period("PT10S"),
        partitions = 1,
        replicants = 1
      )
    )
    .druidTuning(new DruidTuning(75000, 10.seconds, 0))
    .druidBeamConfig(DruidBeamConfigImpl(
      firehoseGracePeriod = 1.minute, 
      firehoseQuietPeriod = 10.seconds, 
      firehoseRetryPeriod = 10.seconds))
    .buildService()

  val buffer = scala.collection.mutable.ListBuffer.empty[Event]
  val maxBufferSize = 10
  while (true) {
    val event = Event.newRandomEvent
    // println(event)
    buffer += event
    if (buffer.size >= maxBufferSize) {
      println(s"Sending ${buffer.size} events to Druid...")
      val t1 = System.nanoTime
      druidService(buffer)
      val t2 = System.nanoTime
      println(s"Sent ${buffer.size} events to Druid in ${(t2-t1)/1e6} msec")
      buffer.clear()
    }
    Thread.sleep(100)
  }
}
