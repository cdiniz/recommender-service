package utils

import akka.actor.{ActorRef, Props}
import recommender.{Recommender, RecommenderTrainer}

trait RecommendationsModule {
  val recommenderActor: ActorRef
  val trainerActor: ActorRef
}


trait RecommendationsModuleImpl extends RecommendationsModule {
  this: Configuration with ActorModule with SparkModule =>
  val recommenderActor = system.actorOf(Props(classOf[Recommender], this), "recommender" )
  val trainerActor = system.actorOf(Props(classOf[RecommenderTrainer], this), "recommender-trainer" )
}