import akka.http.scaladsl.Http
import akka.http.scaladsl.server.RouteConcatenation
import akka.stream.ActorMaterializer
import rest.Recommendations
import akka.actor.Props
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import utils._
import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Main extends App with RouteConcatenation {
  // configuring modules for application, cake pattern for DI
  val modules = new ConfigurationModuleImpl  with ActorModuleImpl with SparkModuleLocalImpl with RecommendationsModuleImpl

  implicit val system = modules.system
  implicit val materializer = ActorMaterializer()
  implicit val ec = modules.system.dispatcher

  val bindingFuture = Http().bindAndHandle(
    cors() (new Recommendations(modules).routes ~ SwaggerDocService.assets ~
    SwaggerDocService.routes), "localhost", 8080)

  println(s"Server online at http://localhost:8080/")

}