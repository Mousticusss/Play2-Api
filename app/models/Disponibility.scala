package models

import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.play.json.collection.JSONCollection

/**
 * Created by marcus on 17/01/17.
 */
case class Disponibility(

  nextDispo: Seq[String]

)

object Disponibility {

  implicit val disponibilityFormat = Json.format[Disponibility]

}