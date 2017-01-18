package services
import javax.inject.Inject

import reactivemongo.play.json._
import models.User
import org.joda.time.DateTime
import play.api.Logger
import play.api.libs.json._
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson._
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection
import reactivemongo.bson.document

import scala.concurrent.{ ExecutionContext, Future }
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.{ Cursor, ReadPreference }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{ Failure, Success }

/**
 * Created by marcus on 26/10/16.
 */

trait UserService extends CRUDService[User] {

}

class UserServices @Inject() (reactiveMongoApi: ReactiveMongoApi) extends MongoCRUDService[User](User.userFormat: Format[User]) with UserService {

  def collection(implicit ec: ExecutionContext) = reactiveMongoApi.database.map(_.collection[JSONCollection]("user"))

  override def find(selector: JsObject)(implicit ec: ExecutionContext): Future[Option[User]] = {

    collection.flatMap(_.find(selector).one[User])

  }

  def update(selector: JsObject, document: User)(implicit ec: ExecutionContext): Future[WriteResult] = {

    collection.flatMap(_.update(selector, document))

  }

}
