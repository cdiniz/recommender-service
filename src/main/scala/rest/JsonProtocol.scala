package rest


import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import recommender.Recommender
import spray.json.DefaultJsonProtocol

trait JsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit def recommendationFormat = jsonFormat2(Recommender.Recommendation)
  implicit def recommendationsFormat = jsonFormat1(Recommender.Recommendations)
}