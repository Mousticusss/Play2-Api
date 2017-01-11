package services

import api.ApiError._
import models.{ Identity, User }
import play.api.Logger
import play.api.libs.json.{ JsObject, Reads, Writes }
import reactivemongo.api.{ Cursor, ReadPreference }
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson._
import reactivemongo.play.json.collection.JSONCollection
import reactivemongo.play.json._

import scala.concurrent.{ ExecutionContext, Future }

/**
 * Created by marcus on 21/10/16.
 */
/**
 * Generic async CRUD service trait
 * @param E type of entity
 * @param ID type of identity of entity (primary key)
 */
trait CRUDService[E] {
  def find(selector: JsObject)(implicit ec: ExecutionContext): Future[Option[E]]

  def findAll(selector: JsObject)(implicit ec: ExecutionContext): Future[List[JsObject]]

  def update(selector: JsObject, update: JsObject)(implicit ec: ExecutionContext): Future[WriteResult]

  def remove(document: JsObject)(implicit ec: ExecutionContext): Future[WriteResult]

  def save(document: E)(implicit ec: ExecutionContext): Future[WriteResult]
}
import play.api.libs.json._

abstract class MongoCRUDService[E: Format](domainFormat: Format[E]) extends CRUDService[E] {

  import reactivemongo.play.json._
  import reactivemongo.play.json.collection.JSONCollection

  //protected def collection =reactiveMongoApi.database.map(_.collection[JSONCollection]("user"))
  def collection(implicit ec: ExecutionContext): Future[JSONCollection]

  def find(selector: JsObject)(implicit ec: ExecutionContext): Future[Option[E]] = {

    //val cursor: Cursor[E] = collection.find(Json.obj()).cursor[E]

    collection.flatMap(_.find(selector).one[E])

  }
  def findAll(selector: JsObject)(implicit ec: ExecutionContext): Future[List[JsObject]] =
    collection.flatMap(_.find(Json.obj()).cursor[JsObject]().collect[List]())
  //collection.find(Json.obj()).cursor[JsObject](ReadPreference.Primary).collect[List]()

  def update(selector: JsObject, update: JsObject)(implicit ec: ExecutionContext): Future[WriteResult] = collection.flatMap(_.update(selector, update.as[JsObject]))

  def remove(doc: JsObject)(implicit ec: ExecutionContext): Future[WriteResult] = collection.flatMap(_.remove(doc, firstMatchOnly = true))

  def save(entity: E)(implicit ec: ExecutionContext): Future[WriteResult] = {

    val doc = Json.toJson(entity).as[JsObject]
    val dodc = Json.toJson(entity).as[JsObject]
    /*  val jsson = document(
      doc
    )*/

    // Logger.debug(entity.toString())
    // Logger.debug(Json.obj(doc).toString())

    Logger.debug(Json.toJson(entity).as[JsObject].toString())
    // collection.flatMap(_.insert(entity))
    // collection.flatMap(_.insert(doc))
    // Logger.debug(Json.toJson(doc).toString())

    domainFormat.writes(entity) match {
      case d @ JsObject(_) => Logger.debug(d.toString())
      case _ =>
        Future.failed[WriteResult](new Exception("cannot write object"))
    }
    domainFormat.writes(entity) match {
      case d @ JsObject(_) => collection.flatMap(_.insert(d))
      case _ =>
        Future.failed[WriteResult](new Exception("cannot write object"))
    }

  }

  // def test(entity: E)(implicit ec: ExecutionContext): JsObject = Json.toJson(entity).as[JsObject]

  /*
  def findByEmail(email: String): Future[Option[models.User]] = collection.flatMap(_.find(document(
    "email" -> email
  )).one[models.User])

*/

}
