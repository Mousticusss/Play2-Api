package models

/**
 * Created by marcus on 21/09/16.
 */

import java.io.File
import java.util.Date

import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.ExecutionContext.Implicits.global
import org.joda.time.DateTime

case class Talent(

  photo: Seq[Byte],
  skill: Seq[String],
  notation: Seq[String],
  job:  Seq[Job],
  disponibility:  Option[Disponibility]
)

object Talent {

  implicit val talentFormat = Json.format[Talent]

}
