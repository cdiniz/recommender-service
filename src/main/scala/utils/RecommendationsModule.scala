package utils

import akka.actor.{ActorRef, Props}
import recommender.Recommender

trait RecommendationsModule {
  val recommenderActor: ActorRef
}


trait RecommendationsModuleImpl extends RecommendationsModule {
  this: Configuration with ActorModule with SparkModule =>
  val recommenderActor = system.actorOf(Props(classOf[Recommender], this), "recommender" )
}