package rest

import akka.actor.{ActorRef, Props}
import akka.http.scaladsl.model.StatusCodes._
import recommender.Recommender
import recommender.Recommender.Recommendations

class RoutesSpec extends AbstractRestTest with JsonProtocol {

  def actorRefFactory = system
  val modules = new Modules {
  }
  val recommendation = new recommendations(modules)

  "Recommendation Routes" should {

    "return an recommendations" in {

      Get("/recommendations/1") ~> recommendation.routes ~> check {
        handled shouldEqual true
        status shouldEqual OK
        responseAs[Recommendations].items.length shouldEqual 0
      }
    }


  }

}