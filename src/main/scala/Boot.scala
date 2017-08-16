import akka.actor.Props
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.RouteConcatenation
import akka.stream.ActorMaterializer
import recommender.Recommender
import rest.recommendations
import utils._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Main extends App with RouteConcatenation with CorsSupport{
  // configuring modules for application, cake pattern for DI
  val modules = new ConfigurationModuleImpl  with ActorModuleImpl with SparkModuleLocalImpl with RecommendationsModuleImpl

  implicit val system = modules.system
  implicit val materializer = ActorMaterializer()
  implicit val ec = modules.system.dispatcher

  val swaggerService = new SwaggerDocService(system)

  val bindingFuture = Http().bindAndHandle(
    new recommendations(modules).routes ~
    swaggerService.assets ~
    corsHandler(swaggerService.routes), "localhost", 8080)

  println(s"Server online at http://localhost:8080/")

}