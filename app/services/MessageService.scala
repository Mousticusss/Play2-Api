package services

import javax.inject.Inject

import models.{ Message, User }
import play.api.libs.json.{ Format, JsObject, Json }
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.commands.UpdateWriteResult
import reactivemongo.bson.BSONObjectID
import reactivemongo.play.json.collection.JSONCollection
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ ExecutionContext, Future }

/**
 * Created by marcus on 20/12/16.
 */

trait MessageService extends CRUDService[Message] {

}

class MessageServices @Inject() (reactiveMongoApi: ReactiveMongoApi) extends MongoCRUDService[Message](Message.messageFormat: Format[Message]) with MessageService {

  def collection(implicit ec: ExecutionContext) = reactiveMongoApi.database.map(_.collection[JSONCollection]("user"))

  def postMessage(message: Message, id: Option[BSONObjectID])(implicit ec: ExecutionContext): Future[UpdateWriteResult] = {

    val newMessageSender = Json.obj(
      "$push" -> Json.obj(
        "messageSend" -> message
      )
    )
    val newMessageRecipient = Json.obj(
      "$push" -> Json.obj(
        "messageReceive" -> message
      )
    )
    collection.flatMap(_.update(Json.obj("_id" -> id), newMessageSender))
    collection.flatMap(_.update(Json.obj("_id" -> id), newMessageRecipient))
    /*  collection.update(
      Json.obj("name" -> name),
      newPhone
    )

    collection.flatMap(_.find(selector).one[Message])*/

  }

  def getMessage(id: Option[BSONObjectID])(implicit ec: ExecutionContext): Future[Option[User]] = {

    collection.flatMap(_.find(Json.obj("_id" -> id)).one[User])
    //  collection.flatMap(_.update(Json.obj("_id" -> id), newMessageRecipient))
    /*  collection.update(
      Json.obj("name" -> name),
      newPhone
    )

    collection.flatMap(_.find(selector).one[Message])*/

  }
}