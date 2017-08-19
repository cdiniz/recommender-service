package recommender

import akka.actor.{Actor, ActorLogging}
import akka.pattern.ask
import akka.util.Timeout
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel
import recommender.Recommender.{GenerateRecommendations, Recommendation, Recommendations}
import recommender.RecommenderTrainer.{ModelUpdated, Train}
import utils.{Configuration, RecommendationsModule, SparkModule}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object Recommender {
  case class GenerateRecommendations(userId: Int, options: Int)

  case class Recommendation(contentItemId: Int, rating: Double)

  case class Recommendations(items: Seq[Recommendation])

}

class Recommender(modules: Configuration with SparkModule with RecommendationsModule) extends Actor with ActorLogging {
  implicit val timeout: Timeout = 120 seconds
  var model: Option[MatrixFactorizationModel] = None

  private def loadModel(): Unit = {
    (modules.trainerActor ? Train).mapTo[ModelUpdated].onSuccess { case ModelUpdated(m) =>
      model = Some(m)
    }
  }

  private def generateRecommendations(userId: Int, options: Int): Recommendations = {
    val results = model match {
      case Some(m) => m.recommendProducts(userId, options)
        .map(rating => Recommendation(rating.product, rating.rating))
        .toList

      case None => Nil
    }
    Recommendations(results)
  }

  override def preStart(): Unit = {
    super.preStart()
    loadModel()
  }

  override def receive : Receive = {
    case GenerateRecommendations(userId, options) =>
      val sdr = sender()
      sdr ! (generateRecommendations(userId, options))
  }

}
