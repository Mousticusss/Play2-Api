package controllers

/**
 * Created by marcus on 20/12/16.
 */

import java.util.UUID

import play.api.libs.functional.syntax._
import api.ApiError._
import models.{ Folder, Message, User }
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
import reactivemongo.bson.{ BSONDocument, BSONDocumentReader, BSONDocumentWriter, BSONObjectID, Macros, document }
import services.{ MessageServices, TokenServices, UserServices }

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success }

class Messages @Inject() (val messagesApi: MessagesApi, val reactiveMongoApi: ReactiveMongoApi, val messageService: MessageServices, val tokenServices: TokenServices, userService: UserServices) extends api.ApiController {

  def delete() = SecuredApiActionWithBody { implicit request =>

    readFromRequest[Array[String]] {

      case (messageReceiveId) => messageService.removeMessage(request.userId, messageReceiveId).flatMap {
        case wr if wr.ok =>
          ok("deleted")
        case wr => errorCustom("api.error.message.delete")
      }
      case _ => errorCustom("api.error.message.delete")
    }
  }

  implicit val postMessageInfoReads: Reads[Tuple2[BSONObjectID, Message]] = (
    (__ \ "idRec").read[BSONObjectID] and
      (__ \ "message").read[Message] tupled
  )
  def postMessage() = SecuredApiActionWithBody { implicit request =>

    readFromRequest[Tuple2[BSONObjectID, Message]] {
      case (idRec, message) =>
        messageService.postMessage(Some(idRec), request.userId, message).flatMap { _ =>
          noContent()
        }

    }

  }
  def info(id: Long) = SecuredApiAction { implicit request =>
    maybeItem(Folder.findById(id))
  }

  def getMessage() = SecuredApiAction { implicit request =>

    userService.find(Json.obj("_id" -> request.userId))

      .flatMap(user => {
        if (user.isDefined)
          ok(user.get.messageReceive)
        else
          errorCustom("api.error.message") //todo Improve
      })

  }
}

