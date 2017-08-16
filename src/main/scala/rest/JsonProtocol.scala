package rest

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import recommender.Recommender.{Recommendation, Recommendations}
import spray.json.DefaultJsonProtocol

trait JsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit def recommendationFormat = jsonFormat2(Recommendation)
  implicit def recommendationsFormat = jsonFormat1(Recommendations)
}