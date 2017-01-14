package controllers

import java.util.Date

import api.ApiError._
import api.JsonCombinators._
import models.ApiToken
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.Play.current
import akka.actor.ActorSystem

import scala.concurrent.duration._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject

import play.api.i18n.MessagesApi
import play.modules.reactivemongo.ReactiveMongoApi
import models.User
import org.joda.time.DateTime
import play.api.Logger
import reactivemongo.bson._
import services.UserServices

class Auth @Inject() (val messagesApi: MessagesApi, val reactiveMongoApi: ReactiveMongoApi, system: ActorSystem, userService: UserServices) extends api.ApiController {

  implicit val loginInfoReads: Reads[Tuple2[String, String]] = (
    (__ \ "email").read[String](Reads.email) and
      (__ \ "password").read[String] tupled
  )

  //def userService = new UserServices(reactiveMongoApi)

  def signIn = ApiActionWithBody { implicit request =>

    readFromRequest[Tuple2[String, String]] {
      case (email, pwd) =>

        userService.find(Json.obj("email" -> email)).flatMap {

          case Some(users) => {

            if (users.password != pwd) errorUserNotFound
            else if (!users.emailConfirmed) errorUserEmailUnconfirmed
            else if (!users.active) errorUserInactive
            else ApiToken.create(request.apiKeyOpt.get, users._id).flatMap { token =>

              ok(Json.obj(
                "token" -> token,
                "minutes" -> 10
              ))
            }
          }
          case None =>

            errorUserNotFound
        }
    }
  }

  def signOut = SecuredApiAction { implicit request =>
    ApiToken.delete(request.token).flatMap { _ =>
      noContent()
    }
  }

  implicit val signUpInfoReads: Reads[Tuple3[String, String, User]] = (
    (__ \ "email").read[String](Reads.email) and
      (__ \ "password").read[String](Reads.minLength[String](6)) and
      (__ \ "user").read[User] tupled
  )

  def signUp = ApiActionWithBody { implicit request =>

    readFromRequest[Tuple3[String, String, User]] {

      case (email, password, user) =>
        userService.find(Json.obj("email" -> email)).flatMap {
          case Some(anotherUser) => errorCustom("api.error.signup.email.exists")

          case None =>
            Logger.debug(user.firstName);
            val a = new User(
              Some(BSONObjectID.generate),
              email,
              password,
              user.firstName,
              user.lastName,
              DateTime.now(),
              DateTime.now(),
              true,
              true,
              Nil,
              Nil,
              None
            )
            val d = a.inscriptionDate;
            userService.save(new User(
              Some(BSONObjectID.generate),
              email,
              password,
              user.firstName,
              user.lastName,
              DateTime.now(),
              DateTime.now(),
              true,
              true,
              Nil,
              Nil,
              None
            ))
              .flatMap {
                case wr if wr.ok =>
                  Logger.debug("ok" + user.toString)
                  // Send confirmation email. You will have to catch the link and confirm the email and activate the user.
                  // But meanwhile...
                  system.scheduler.scheduleOnce(30 seconds) {
                    //  User.confirmEmail(id)
                  }

                  ok("user save")
                case wr => errorCustom(wr.writeErrors.toString())

              }
          // nothing special, delegate to our original showNotification function
          case _ =>
            Logger.debug(Some.toString)
            errorCustom("api.error.signup.email.exists") // nothing special, delegate to our original showNotification function

        }
    }
  }

}