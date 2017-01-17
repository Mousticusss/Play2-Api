package services

import java.util.UUID
import javax.inject.Inject

import models.{Message, User}
import play.api.libs.json.{Format, JsObject, Json}
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.commands.UpdateWriteResult
import reactivemongo.bson.BSONObjectID
import reactivemongo.play.json.collection.JSONCollection
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}

/**
 * Created by marcus on 20/12/16.
 */

trait MessageService extends CRUDService[Message] {

}

class MessageServices @Inject() (reactiveMongoApi: ReactiveMongoApi) extends MongoCRUDService[Message](Message.messageFormat: Format[Message]) with MessageService {

  def collection(implicit ec: ExecutionContext) = reactiveMongoApi.database.map(_.collection[JSONCollection]("user"))

  def postMessage(idR:Option[BSONObjectID],id: Option[BSONObjectID],message: Message )(implicit ec: ExecutionContext): Future[UpdateWriteResult] = {




    //Todo so much work for almost nothing maybe create the uuid in the write json
    val mS = message.copy(idMessage = Some(UUID.randomUUID().toString))
    val mR = message.copy(idMessage = Some(UUID.randomUUID().toString))
    val newMessageSender = Json.obj(
      "$push" -> Json.obj(
        "messageSend" -> mS
      )
    )
    val newMessageRecipient = Json.obj(
      "$push" -> Json.obj(
        "messageReceive" -> mR
      )
    )


    collection.flatMap(_.update(Json.obj("_id" -> id), newMessageSender))
    collection.flatMap(_.update(Json.obj("_id" -> idR), newMessageRecipient))

  }


   def getMessage(id: Option[BSONObjectID])(implicit ec: ExecutionContext): Future[Option[User]] = {

    collection.flatMap(_.find(Json.obj("_id" -> id)).one[User])

  }

  def removeMessage(userID:Option[BSONObjectID],messageId:Array[String]) ={


    /*
    update({"_id":ObjectId("585a9b466300008004cc5906")},{"$pull":{"messageSend":{"idMessage"
      :
    {
      $in: [ "id1", "id2" ] }
    }}})*/

    val selector = Json.obj(
      "$_id" -> userID    )
    val pull = Json.obj(
      "$$pull" -> Json.obj(
        "messageReceive" -> Json.obj("idMessage" -> Json.obj( "$in"-> messageId) )
      )
    )

    collection.flatMap(_.update(selector,pull))


  }
}