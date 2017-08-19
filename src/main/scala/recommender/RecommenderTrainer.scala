package recommender

import akka.actor.{Actor, ActorLogging}
import org.apache.spark.mllib.recommendation.{ALS, Rating}
import org.apache.spark.rdd.RDD
import recommender.RecommenderTrainer.{ModelUpdated, Train}
import utils.{Configuration, RecommendationsModule, SparkModule}

object RecommenderTrainer {
  case object Train
  case object ModelUpdated
}

class RecommenderTrainer(modules: Configuration with SparkModule with RecommendationsModule) extends Actor with ActorLogging {

  override def receive: Receive = {

    case Train =>
      val sdr = sender()
      val sc = modules.spark().sparkContext
      val data = sc.textFile("src/main/resources/ratings.csv")
      val ratings: RDD[Rating] = data.map(_.split(',') match { case Array(user, item, rate, _) =>
      Rating(user.toInt, item.toInt, rate.toDouble)
    })

    val numRatings = ratings.count()
    val numUsers = ratings.map(_.user).distinct().count()
    val numMovies = ratings.map(_.product).distinct().count()

    println(s"Got $numRatings ratings from $numUsers users on $numMovies movies.")

    val splits = ratings.randomSplit(Array(0.8, 0.2))
    val training = splits(0).cache()
    val implicitPrefs = true
    val test = if (implicitPrefs) {
      splits(1).map(x => Rating(x.user, x.product, if (x.rating > 0) 1.0 else 0.0))
    } else {
      splits(1)
    }
      .cache()

    val numTraining = training.count()
    val numTest = test.count()
    println(s"Training: $numTraining, test: $numTest.")

    ratings.unpersist(blocking = false)

    val model = new ALS()
      .setRank(10)
      .setIterations(10)
      .setLambda(1.0)
      .setImplicitPrefs(implicitPrefs)
      .run(training)

    model.save(sc,"src/main/resources/model")
    sdr ! ModelUpdated
  }

}
