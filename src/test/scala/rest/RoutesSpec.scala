package rest

import akka.http.scaladsl.model.StatusCodes._
import recommender.Recommender

class RoutesSpec extends AbstractRestTest with JsonProtocol {

  def actorRefFactory = system
  val modules = new Modules {
  }
  val recommendation = new Recommendations(modules)

  "Recommendation Routes" should {

   /** "should train model" in {

      Get("/recommendations/train") ~> recommendation.routes ~> check {
        handled shouldEqual true
        status shouldEqual OK
      }

    } **/

    "return an recommendations" in {

      Get("/recommendations/1") ~> recommendation.routes ~> check {
        handled shouldEqual true
        status shouldEqual OK
        responseAs[Recommender.Recommendations].items.length shouldEqual 0
      }


    }

  }

}