{
  "type": "index_realtime",
  "id": "twitter",
  "spec": {
    "dataSchema": {
      "dataSource": "tweets",
      "parser": {
        "type": "string",
        "parseSpec": {
          "type": "json",
          "format": "json",
          "timestampSpec": {
            "column": "timestamp",
            "format": "millis"
          },
          "dimensionsSpec": {
            "dimensions": [
              "language",
              "utcOffset",
              "hashtags",
              "urls",
              "userId"
            ],
            "dimensionExclusions": [],
            "spatialDimensions": [
              {
                "dimName": "geo",
                "dims": [
                  "latitude",
                  "longitude"
                ]
              }
            ]
          }
        }
      },
      "granularitySpec": {
        "segmentGranularity": "minute",
        "queryGranularity": "none"
      },
      "metricsSpec": [
        {
          "name": "tweets",
          "type": "count"
        },
        {
          "fieldName": "retweetCount",
          "name": "total_retweet_count",
          "type": "longSum"
        },
        {
          "fieldName": "favoriteCount",
          "name": "total_favorite_count",
          "type": "longSum"
        },
        {
          "fieldName": "text",
          "name": "text_hll",
          "type": "hyperUnique"
        },
        {
          "fieldName": "userId",
          "name": "user_id_hll",
          "type": "hyperUnique"
        },
        {
          "fieldName": "hashtags",
          "name": "hashtags_hll",
          "type": "hyperUnique"
        },
        {
          "fieldName": "urls",
          "name": "urls_hll",
          "type": "hyperUnique"
        },
        {
          "fieldName": "retweet_count",
          "name": "min_retweet_count",
          "type": "min"
        },
        {
          "fieldName": "retweet_count",
          "name": "max_retweet_count",
          "type": "max"
        },
        {
          "fieldName": "favoriteCount",
          "name": "min_favorite_count",
          "type": "min"
        },
        {
          "fieldName": "favoriteCount",
          "name": "max_favorite_count",
          "type": "max"
        }
      ]
    },
    "ioConfig": {
      "type": "realtime",
      "firehose": {
        "type": "kafka-0.8",
        "consumerProps": {
          "zookeeper.connect": "192.168.59.103:2181/kafka",
          "zookeeper.connection.timeout.ms": "15000",
          "zookeeper.session.timeout.ms": "15000",
          "zookeeper.sync.time.ms": "5000",
          "group.id": "druid-twitter-realtime",
          "fetch.message.max.bytes": "1048586",
          "auto.offset.reset": "largest",
          "auto.commit.enable": "false"
        },
        "feed": "twitter-statuses"
      },
      "plumber": {
        "type": "realtime"
      }
    },
    "tuningConfig": {
      "type": "realtime",
      "maxRowsInMemory": 500000,
      "intermediatePersistPeriod": "PT10m",
      "windowPeriod": "PT10m",
      "basePersistDirectory": "/tmp/realtime/basePersist",
      "rejectionPolicy": {
        "type": "serverTime"
      }
    }
  }
}