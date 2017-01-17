package models

/**
 * Created by marcus on 20/12/16.
 */

import java.util.Date

import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.ExecutionContext.Implicits.global
import org.joda.time.DateTime

case class Message(

  sender: Option[String],
  recipient: Option[String],
  recipientId: Option[String],
  objectM: Option[String],
  dateSend: Option[DateTime],
  dateRead: Option[DateTime],
  corpus: Option[String]
)

object Message {

  def collection(implicit reactiveMongoApi: ReactiveMongoApi) = reactiveMongoApi.database.map(_.collection[JSONCollection]("message"))

  implicit val messageFormat = Json.format[Message]

}
