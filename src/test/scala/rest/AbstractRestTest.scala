package rest

import akka.actor.{ActorRef, Props}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark.SparkContext
import org.scalatest.{Matchers, WordSpec}
import org.specs2.mock.Mockito
import recommender.{Recommender, RecommenderTrainer}
import utils._

trait AbstractRestTest  extends WordSpec with Matchers with ScalatestRouteTest with Mockito{

  trait Modules extends ConfigurationModuleImpl with ActorModule with SparkModuleLocalImpl with RecommendationsModule {
    override val system = AbstractRestTest.this.system
    override val recommenderActor: ActorRef = system.actorOf(Props(classOf[Recommender], this))
    override val trainerActor: ActorRef = system.actorOf(Props(classOf[RecommenderTrainer], this))

  }

  def getConfig: Config = ConfigFactory.empty()


}
