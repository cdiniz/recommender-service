package rest

import javax.ws.rs.Path

import akka.http.scaladsl.server.{Directives, Route}
import io.swagger.annotations._
import utils.{Configuration, RecommendationsModule}
import akka.pattern.ask
import akka.util.Timeout
import recommender.Recommender
import recommender.Recommender.{GenerateRecommendations}

import scala.concurrent.duration._

@Path("/recommendations")
@Api(value = "/recommendations", produces = "application/json")
class Recommendations(modules: Configuration with RecommendationsModule)  extends Directives with JsonProtocol{
  implicit val timeout: Timeout = 60 seconds

  @Path("/{id}")
  @ApiOperation(value = "Return Recommendations for a user", notes = "", nickname = "", httpMethod = "GET")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "id", value = "User Id", required = true, dataType = "int", paramType = "path")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return Recommendations", response = classOf[Recommendations]),
    new ApiResponse(code = 400, message = "The user id should be greater than zero"),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def supplierGetRoute =
    path("recommendations" / IntNumber) { (id) =>
      get {
        validate(id > 0, "The user id should be greater than zero") {
          complete {
            (
              modules.recommenderActor ? GenerateRecommendations(id, 3)
            ).mapTo[Recommender.Recommendations]
          }
        }
      }
    }

    val routes: Route =  supplierGetRoute //~ supplierTrainRoute

}

