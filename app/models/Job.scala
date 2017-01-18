package models

import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.play.json.collection.JSONCollection

/**
 * Created by marcus on 17/01/17.
 */
case class Job(
  society: String,
  jobName: String,
  resume: String

)

object Job {

  implicit val jobFormat = Json.format[Job]

}