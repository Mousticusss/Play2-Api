package models

import play.api.libs.json.{ JsObject, Json, _ }
import play.api.libs.json.{ JsPath, Json, Reads, Writes }
import java.util.{ Date, UUID }

import play.api.libs.json.{ JsObject, Json, _ }
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.play.json.collection.JSONCollection

import reactivemongo.play.json.BSONFormats._
import scala.concurrent.ExecutionContext.Implicits.global
import org.joda.time.DateTime
import play.api.Logger
import reactivemongo.bson.{ BSONDateTime, BSONObjectID }
import play.api.libs.functional.syntax._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/*
* Stores the Auth Token information. Each token belongs to a Api Key and user
*/
case class ApiToken(

    userId: Option[BSONObjectID],
    token: String, // UUID 36 digits
    apiKey: String,
    expirationTime: DateTime

) {
  def isExpired = expirationTime.isBeforeNow
}

object ApiToken {
  import FakeDB.tokens

  implicit val externalEventRead: Reads[ApiToken] = (
    (JsPath \ "userId").readNullable[BSONObjectID] and
    (JsPath \ "token").read[String] and
    (JsPath \ "apiKey").read[String] and
    (JsPath \ "expirationTime").read[DateTime]

  )(ApiToken.apply _)

  val externalEventWrites: Writes[ApiToken] = (
    (JsPath \ "userId").writeNullable[BSONObjectID] and
    (JsPath \ "token").write[String] and
    (JsPath \ "apiKey").write[String] and
    (JsPath \ "expirationTime").write[DateTime]
  )(unlift(ApiToken.unapply))

  implicit val apiTokenFormat = Json.format[ApiToken]

  def findByTokenAndApiKey(token: String, apiKey: String): Future[Option[ApiToken]] = Future.successful {
    tokens.find(t => t.token == token && t.apiKey == apiKey)
  }

  def create(apiKey: String, userId: Option[BSONObjectID]): Future[String] = Future.successful {
    // Be sure the uuid is not already taken for another token

    def newUUID: String = {
      val uuid = UUID.randomUUID().toString
      if (!tokens.exists(_.token == uuid)) uuid else newUUID
    }
    val token = newUUID

    tokens.insert(_ => ApiToken(userId, token, apiKey, expirationTime = (new DateTime()) plusMinutes 1000))
    token
  }

  def delete(token: String): Future[Unit] = Future.successful {
    tokens.delete(_.token == token)
  }
}
