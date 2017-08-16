package recommender

import java.nio.file.Path

import akka.actor.{Actor, ActorLogging}
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel
import recommender.Recommender.{GenerateRecommendations, Recommendation, Recommendations}
import utils.{Configuration, SparkModule}

object Recommender {
  case class GenerateRecommendations(userId: Int, options: Int)

  case class Recommendation(contentItemId: Int, rating: Double)

  case class Recommendations(items: Seq[Recommendation])

}

class Recommender(modules: Configuration with SparkModule) extends Actor with ActorLogging {

  var model: Option[MatrixFactorizationModel] = None

  private def loadModel(): Unit = {
    //this.model = Some(MatrixFactorizationModel.load(modules.sc,"file://src/main/resources/model"))
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

  override def receive: Receive = {
    case GenerateRecommendations(userId, options) =>
      sender() ! generateRecommendations(userId, options)
  }

}
