package controllers

import java.io._
import api.ApiError._
import api.JsonCombinators._
import models.{ ApiToken, User }
import play.api.mvc._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject

import play.api.Logger
import play.api.i18n.MessagesApi
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.modules.reactivemongo.ReactiveMongoApi
import services.UserServices
import reactivemongo.play.json.BSONFormats._

class Account @Inject() (val messagesApi: MessagesApi, val reactiveMongoApi: ReactiveMongoApi) extends api.ApiController {

  def userService = new UserServices(reactiveMongoApi)

  // def info = SecuredApiAction { implicit request =>
  // maybeItem(userService.(Json.obj("_id" -> request.userId)))
  //}
  def info() = SecuredApiAction { implicit request =>
    ok("The API is ready")
  }

  def update = SecuredApiActionWithBody { implicit request =>
    readFromRequest[User] { user =>
      userService.update(Json.obj("_id" -> request.userId), Json.obj("lastName" -> user.lastName)).flatMap { wr =>
        if (wr.ok) noContent() else errorInternal
      }
    }
  }

  implicit val pwdsReads: Reads[Tuple2[String, String]] =
    (__ \ "old").read[String](Reads.minLength[String](1)) and
      (__ \ "new").read[String](Reads.minLength[String](6)) tupled

  def updatePassword = SecuredApiActionWithBody { implicit request =>
    readFromRequest[Tuple2[String, String]] {
      case (oldPwd, newPwd) =>
        userService.find(Json.obj("_id" -> request.userId)).flatMap {
          case None => errorUserNotFound
          case Some(user) if (oldPwd != user.password) => errorCustom("api.error.reset.pwd.old.incorrect")
          case Some(user) => userService.update(Json.obj("_id" -> request.userId), Json.obj("password" -> newPwd)).flatMap { wr =>
            if (wr.ok) noContent() else errorInternal
          }
        }
    }
  }

  def updateUser = SecuredApiActionWithBody { implicit request =>
    readFromRequest[User] {
      case (user) =>
        userService.find(Json.obj("_id" -> request.userId)).flatMap {
          case None => errorUserNotFound
          case Some(user) => userService.update(Json.obj("_id" -> request.userId), Json.obj("" -> Json.toJson(user))).flatMap { wr =>
            if (wr.ok) noContent() else errorInternal
          }
        }
    }
  }

  def delete = SecuredApiAction { implicit request =>
    ApiToken.delete(request.token).flatMap { _ =>
      userService.remove(Json.obj("_id" -> request.userId)).flatMap { _ =>
        noContent()
      }
    }
  }

}
object CopyBytes {
  var in = None: Option[FileInputStream]
  var out = None: Option[FileOutputStream]
  try {
    in = Some(new FileInputStream("/tmp/Test.class"))
    out = Some(new FileOutputStream("/tmp/Test.class.copy"))
    var c = 0
    while ({ c = in.get.read; c != (-1) }) {
      out.get.write(c)
    }
  } catch {
    case e: IOException => e.printStackTrace
  } finally {
    println("entered finally ...")
    if (in.isDefined) in.get.close
    if (out.isDefined) out.get.close
  }
}