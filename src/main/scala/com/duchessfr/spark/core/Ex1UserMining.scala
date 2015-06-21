package com.duchessfr.spark.core


import org.apache.spark.{SparkContext, SparkConf}

import org.apache.spark.rdd._
import com.duchessfr.spark.utils.TweetUtils
import com.duchessfr.spark.utils.TweetUtils._

/**
 * The scala API documentation: http://spark.apache.org/docs/latest/api/scala/index.html
 *
 * Now we use another dataset (with 8198 tweets). The data are reduced tweets as the example below:
 *
 * {"id":"572692378957430785",
 * "user":"Srkian_nishu :)",
 * "text":"@always_nidhi @YouTube no i dnt understand bt i loved of this mve is rocking",
 * "place":"Orissa",
 * "country":"India"}
 *
 * We want to make some computations on the users:
 * - find all the tweets by user
 * - find how many tweets each user has
 *
 */
object Ex1UserMining extends App {

  val pathToFile = "data/reduced-tweets.json"

  /**
   *  Load the data from the json file and return an RDD of Tweet
   */
  def loadData(): RDD[Tweet] = {
    // create spark configuration and spark context
    val conf = new SparkConf()
        .setAppName("Wordcount")
        .setMaster("local[*]")

    val sc = new SparkContext(conf)

    // Load the data  and parse it into a Tweet.
    // Look at the Tweet Objetc in the TweetUtils class.
    sc.textFile(pathToFile)
        .mapPartitions(TweetUtils.parseFromJson(_))
        .cache
  }

  /**
   *   Return for each user all his tweets
   */
  def tweetsByUser(): RDD[(String, Iterable[Tweet])] = {
    val tweets = loadData()

    tweets.groupBy(_.user)

  }

  /**
   *  Compute the number of tweets by user
   */
  def tweetByUserNumber(): RDD[(String, Int)] = {
    val tweets = loadData()

    tweets.map(tweet => (tweet.user, 1))
          .reduceByKey(_+_)
  }


  /**
   *  Top 10 twitterers
   */
  def topTenTwitterers(): Array[(String, Int)] = {
    val nbTweetsByUsers = tweetByUserNumber()
    nbTweetsByUsers.sortBy(_._2,false).take(10)

    // or
    //nbTweetsByUsers.top(10)((Ordering.by(m => m._2)))
  }

}
