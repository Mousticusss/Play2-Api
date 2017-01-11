package services

/**
 * Created by marcus on 26/09/16.
 */
import reactivemongo.api.{ DB, DefaultDB, MongoConnection, MongoDriver }
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait ReactiveMongoApi2 {
  def driver2: MongoDriver
  def connectioné: MongoConnection
  def db: DB

  val mongoUri = "mongodb://localhost:27017/mydb?authMode=scram-sha1"
  val driver = MongoDriver()
  val parsedUri = MongoConnection.parseURI(mongoUri)
  val connection = parsedUri.map(driver.connection(_))
  val futureConnection = Future.fromTry(connection)
  def db1: Future[DefaultDB] = futureConnection.flatMap(_.database("firstdb"))
  def db2: Future[DefaultDB] = futureConnection.flatMap(_.database("anotherdb"))
  def personCollection = db1.map(_.collection("person"))
}