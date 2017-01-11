package controllers

/**
 * Created by marcus on 20/12/16.
 */

import api._
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
import reactivemongo.bson.{ BSONDocument, BSONDocumentReader, BSONDocumentWriter, Macros, document }
import services.{ MessageServices, UserServices }

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success }

class Messages @Inject() (val messagesApi: MessagesApi, val reactiveMongoApi: ReactiveMongoApi) extends api.ApiController {

  def messageService = new MessageServices(reactiveMongoApi)
  def userService = new UserServices(reactiveMongoApi)

  def delete(id: Long) = SecuredApiAction { implicit request =>
    messageService.remove(Json.obj("_id" -> id)).flatMap { _ =>
      noContent()
    }
  }
  def postMessage() = SecuredApiActionWithBody { implicit request =>
    Logger.info(DateTime.now().toString());

    readFromRequest[Message] { message =>

      messageService.postMessage(message, request.userId).flatMap { _ =>
        noContent()
      }

    }

  }
  def info(id: Long) = SecuredApiAction { implicit request =>
    maybeItem(Folder.findById(id))
  }

  def getMessage() = SecuredApiAction { implicit request =>
    /*readFromRequest[Tuple3[String, String, User]] {
      val  b= userService.find(Json.obj("_id" -> request.userId))
      b.flatMap(
      case Some(User) => Future.successful(None)//(*)
      case None => Future.successful(None);
      )


    }*/ /*readFromRequest[Tuple3[String, String, User]] {
      val  b= userService.find(Json.obj("_id" -> request.userId))
      b.flatMap(
      case Some(User) => Future.successful(None)//(*)
      case None => Future.successful(None);
      )


    }*/

    userService.find(Json.obj("_id" -> request.userId))
      /*  a onComplete {
      case Success(user) => if (user.isDefined) {
        user.get.messageReceive
      }
      case Failure(t) => println("An error has occured: " + t.getMessage)
    }*/
      .flatMap(user => {
        if (user.isDefined)
          ok(user.get.messageReceive)
        else
          ok("erer") //todo Improve
      })
    //maybeItem(a)

  }
}

