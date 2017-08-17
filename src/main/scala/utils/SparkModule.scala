package utils

import org.apache.spark.sql.SparkSession


trait SparkModule {
  def spark(): SparkSession
}


trait SparkModuleLocalImpl extends SparkModule {
  this: Configuration =>
  import org.apache.spark.sql.SparkSession
  def spark(): SparkSession =
    SparkSession
      .builder()
      .appName("recommender.Recommender")
      .config("spark.master", "local[*]")
      .getOrCreate()
}

trait SparkModuleLocalImplDocker extends SparkModule {
  this: Configuration =>
  import org.apache.spark.sql.SparkSession
  def spark(): SparkSession =
    SparkSession
      .builder()
      .appName("recommender.Recommender")
      .config("spark.master", "spark://localhost:7077")
      .config("spark.driver.port", "7001")
      .config("spark.fileserver.port", "7002")
      .config("spark.broadcast.port", "7003")
      .config("spark.replClassServer.port", "7004")
      .config("spark.blockManager.port", "7005")
      .config("spark.executor.port", "7006")
      .config("spark.port.maxRetries", "4")
      .getOrCreate()
}