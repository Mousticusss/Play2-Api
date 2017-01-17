package services

import java.util.UUID
import javax.inject.Inject

import api.ApiError._
import models.ApiToken
import models.FakeDB._
import org.joda.time.DateTime
import play.api.Logger
import play.api.libs.json.{ Format, JsObject, Json }
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.bson.BSONObjectID
import reactivemongo.play.json.collection.JSONCollection
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success }
import javax.inject.Singleton
/**
 * Created by marcus on 14/01/17.
 */

trait TokenService extends CRUDService[ApiToken] {

}

@Singleton
class TokenServices @Inject() (reactiveMongoApi: ReactiveMongoApi) extends MongoCRUDService[ApiToken](ApiToken.apiTokenFormat: Format[ApiToken]) with TokenService {

  def collection(implicit ec: ExecutionContext) = reactiveMongoApi.database.map(_.collection[JSONCollection]("token"))

  /* override def find(selector: JsObject)(implicit ec: ExecutionContext): Future[Option[ApiToken]] = {

    collection.flatMap(_.find(selector).one[ApiToken])

  }*/

  def findByTokenAndApiKey(token: String, apiKey: String): Future[Option[ApiToken]] = {

    find(Json.obj("token" -> token, "apiKey" -> apiKey))
  }

  def create(apiKey: String, userId: Option[BSONObjectID]): Future[String] = {
    // Be sure the uuid is not already taken for another token

    def newUUID: Future[String] = {
      val uuid = UUID.randomUUID().toString
      find(Json.obj("token" -> uuid)).flatMap {
        tok =>
          if (tok.isEmpty) {
            Future { uuid }
          } else
            newUUID
      }

    }
    val token = newUUID

    token.flatMap {
      token =>
        save(ApiToken(userId, token, apiKey, expirationTime = (new DateTime()) plusMinutes 60 * 24)).map {

          case wr if wr.ok => token

          case wr => ""

        }

    }

  }

  def delete(token: String): Future[Unit] = Future.successful {
    tokens.delete(_.token == token)
  }
}

