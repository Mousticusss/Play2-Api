package controllers

import api._
import api.ApiError._
import models.User
import play.api.libs.json._
import reactivemongo.play.json._
import collection._

import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject

import models.FakeDB._
import play.api.Logger
import play.api.i18n.MessagesApi
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.play.json.collection.JSONCollection
import reactivemongo.bson.{ BSONDocument, BSONDocumentReader, BSONDocumentWriter, Macros, document }
import services.UserServices

import scala.concurrent.{ ExecutionContext, Future }

class Users @Inject() (val messagesApi: MessagesApi, val reactiveMongoApi: ReactiveMongoApi) extends api.ApiController {

  def userService = new UserServices(reactiveMongoApi)

  def usernames = ApiAction { implicit request => ok("The API is ready") }
  def collection = reactiveMongoApi.database.map(_.collection[JSONCollection]("user"))

  def getListUser() = SecuredApiAction { implicit request =>
    userService.remove(Json.obj("_id" -> request.userId)).flatMap { _ =>
      noContent()
    }
  }
  /*  def findById(implicit reactiveMongoApi: ReactiveMongoApi ,id: Long): Future[List[User]] =  collection.flatMap(_.find(document("_id" ->id)). // query builder
      cursor[User]().collect[List]()) // collect using the result cursor
    // ... deserializes the document using personRead
*/

  /*  def findByEmail(email: String): Future[Option[User]] = Future.successful {
    users.find(_.email == email)
  }*/

  // def findByEmail(email: String):Future[Option[User]] =  collection.flatMap(_.find(document("_id" ->email)).one[User]().collect[List]())
  //def collection: BSONCollection
  def findByEmail(email: String)(implicit jsonReads: Reads[User]): Future[Option[User]] = collection.flatMap(_.find(document(

    "email" -> email

  )).one[User])

  /* def insert2(email: String, password: String, name: String): Future[(Long, User)] = Future.successful {
    users.insert(User(_, email, password, name, emailConfirmed = false, active = false))
  }

  def insert(email: String, password: String, name: String): Future[Either[String, User]] = {

    val jUser = User(
      1,
      email,
      password,
      name,
      true,
      false
    )

    collection.flatMap(_.insert(jUser).map {
      case wr if wr.ok => Right(jUser)
      case wr => Left(wr.writeErrors.toString())
    })

  }
*/ def userCollection = db.collection[JSONCollection]("user")

  def findOlder1(email: String): Future[Option[BSONDocument]] = {
    // { "age": { "$gt": 27 } }
    val query = BSONDocument("age" -> BSONDocument("$gt" -> 27))

    // MongoDB .findOne
    userCollection.find(query).one[BSONDocument]
  }

  def findOlder2(collection: BSONCollection) = {
    val query = BSONDocument("age" -> BSONDocument("$gt" -> 27))

    // only fetch the name field for the result documents
    val projection = BSONDocument("name" -> 1)

    collection.find(query, projection).cursor[BSONDocument]().
      collect[List](25) // get up to 25 documents
  }
}

