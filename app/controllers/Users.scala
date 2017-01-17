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
import org.joda.time.DateTime
import play.api.Logger
import play.api.i18n.MessagesApi
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.play.json.collection.JSONCollection
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter, BSONObjectID, Macros, document}
import services.{TokenServices, UserServices}

import scala.concurrent.{ExecutionContext, Future}

class Users @Inject() (val messagesApi: MessagesApi, val reactiveMongoApi: ReactiveMongoApi, val tokenServices: TokenServices) extends api.ApiController {

  def userService = new UserServices(reactiveMongoApi)

  def usernames = ApiAction { implicit request => ok("The API is ready") }
  def collection = reactiveMongoApi.database.map(_.collection[JSONCollection]("user"))

  def getListUser() = SecuredApiAction { implicit request =>
    userService.remove(Json.obj("_id" -> request.userId)).flatMap { _ =>
      noContent()
    }
  }


  def update = SecuredApiActionWithBody { implicit request =>

    readFromRequest[User] {

      case (user) =>
        userService.update(Json.obj("_id" -> request.userId),Json.obj("user" -> request.userId)).flatMap {
          case wr if wr.ok =>            ok("user update")

          case wr => errorCustom(wr.writeErrors.toString())

        }
      case _ =>

        errorCustom("api.error.no.user") // nothing special, delegate to our original showNotification function


    }
  }
  /*def remove = SecuredApiAction { implicit request =>



      case (user) =>
        userService.update(Json.obj("_id" -> request.userId),Json.obj("user" -> request.userId)).flatMap {
          case wr if wr.ok =>            ok("user update")

          case wr => errorCustom(wr.writeErrors.toString())

        }
      case _ =>

        errorCustom("api.error.no.user") // nothing special, delegate to our original showNotification function



  }*/

}

