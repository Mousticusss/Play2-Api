package api

import models._
import java.util.Date
import play.api.libs.json._
import play.api.libs.json.Reads.{ DefaultDateReads => _, _ } // Custom validation helpers
import play.api.libs.functional.syntax._
import reactivemongo.play.json.BSONFormats._
/*
* Set of every Writes[A] and Reads[A] for render and parse JSON objects
*/
object JsonCombinators {

  // implicit val dateWrites = Writes.dateWrites("dd-MM-yyyy HH:mm:ss")
  //implicit val dateReads = Reads.dateReads("dd-MM-yyyy HH:mm:ss")

  //implicit val yourJodaDateReads = Reads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss'Z'")
  //implicit val yourJodaDateWrites = Writes.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss'Z'")
  implicit val userWrites = new Writes[User] {
    def writes(u: User) = Json.obj(
      "_id" -> u._id,
      "email" -> u.email,
      //"firstName" -> u.firstName,
      "lastName" -> u.lastName
    //  "inscriptionDate" -> u.inscriptionDate,
    //"lastConnection" -> u.lastConnection
    )
  }
  implicit val userReads: Reads[User] = (
    (__ \ "firstName").read[String](minLength[String](1)) and
    (__ \ "lastName").read[String](minLength[String](1))
  )((firstName, lastName) => User(null, null, null, firstName, lastName, null, null, false, false, null, null, null))

  implicit val folderWrites = new Writes[Folder] {
    def writes(f: Folder) = Json.obj(
      "id" -> f.id,
      "userId" -> f.userId,
      "order" -> f.order,
      "name" -> f.name
    )
  }
  implicit val folderReads: Reads[Folder] =
    (__ \ "name").read[String](minLength[String](1)).map(name => Folder(0L, 0L, 0, name))

  implicit val taskWrites = new Writes[Task] {
    def writes(t: Task) = Json.obj(
      "id" -> t.id,
      "folderId" -> t.folderId,
      "order" -> t.order,
      "text" -> t.text,
      "date" -> t.date,
      "deadline" -> t.deadline,
      "done" -> t.done
    )
  }
  implicit val taskReads: Reads[Task] = (
    (__ \ "text").read[String](minLength[String](1)) and
    (__ \ "deadline").readNullable[Date]
  )((text, deadline) => Task(0L, 0L, 0, text, null, deadline, false))

}