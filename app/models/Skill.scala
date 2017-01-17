package models

import play.api.libs.json.Json

/**
  * Created by marcus on 17/01/17.
  */
case class Skill (

                   dev:  Seq[String],
                   other:    Seq[String]




)

object Skill {

  implicit val skillFormat = Json.format[Skill]

}