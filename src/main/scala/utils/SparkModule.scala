package utils

import org.apache.spark.SparkContext


trait SparkModule {
  val sc: SparkContext
}


trait SparkModuleLocalImpl extends SparkModule {
  this: Configuration =>
  import org.apache.spark.sql.SparkSession
  val spark: SparkSession =
    SparkSession
      .builder()
      .appName("Recommender")
      .config("spark.master", "local")
      .getOrCreate()
  val sc = spark.sparkContext
}