package models

import java.io.File
import java.util.{ Date, UUID }

import play.api.libs.json.{ JsObject, Json, _ }
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.ExecutionContext.Implicits.global
import org.joda.time.DateTime
import play.api.Logger
import reactivemongo.play.json.BSONFormats._
import reactivemongo.bson.{ BSONDateTime, BSONObjectID }
import play.api.libs.functional.syntax._
case class User(
  _id: Option[BSONObjectID],
  email: String,
  password: String,
  firstName: String,
  lastName: String,
  inscriptionDate: DateTime,
  lastConnection: DateTime,
  emailConfirmed: Boolean,
  active: Boolean,
  messageSend: Seq[Message],
  messageReceive: Seq[Message],
  talent: Option[Talent]
)

object User {

  def collection(implicit reactiveMongoApi: ReactiveMongoApi) = reactiveMongoApi.database.map(_.collection[JSONCollection]("user"))

  /*  implicit val userFormat = Json.format[User]
  // val JSON_KEY_ID = "_id"
  /*implicit val userDaoWrites: OWrites[User] = new OWrites[User] {
    def writes(user: User): JsObject = Json.obj(
      JSON_KEY_ID -> JsString(user.id.toString)
    )
  }*/
  // implicit val yourJodaDateReads = Reads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss'Z'")
  //implicit val yourJodaDateWrites = Writes.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss'Z'")
  val dateTimePattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ"
  val externalDateTimeReads = play.api.libs.json.Reads.jodaDateReads(dateTimePattern)
  val externalDateTimeWrites = play.api.libs.json.Writes.jodaDateWrites(dateTimePattern)

  def read(time: BSONDateTime) = new DateTime(time.value)
  def write(jdtime: DateTime) = BSONDateTime(jdtime.getMillis)
*/

  /*implicit object UserIdentity extends Identity[User] {
    val name = "uuid"
    def set(entity: User): User = entity.copy()
    /*    def of(entity: User): Option[UUID] = entity.id

   */ //  def clear(entity: User): User = entity.copy(id = None)*/
  // def next: UUID = UUID.randomUUID()
  // }*/

  /* implicit object FopReads extends Format[User] {
    def reads(json: JsValue) = JsSuccess(new User(
      (json \ "id").as[Int],
      (json \ "email").as[String],
      (json \ "password").as[String],
      (json \ "name").as[String],
      (json \ "emailConfirmed").as[Boolean],
      (json \ "active").as[Boolean]
    ))
    def writes(ts: User) = JsObject(Seq(
      "id" -> JsNumber(ts.id),
      "email" -> JsString(ts.email),
      "password" -> JsString(ts.password),
      "name" -> JsString(ts.name),
      "emailConfirmed" -> JsBoolean(ts.emailConfirmed),
      "active" -> JsBoolean(ts.active)
    ))




  }*/

  implicit val dateTimeReads = new Reads[DateTime] {
    def reads(jv: JsValue) = {
      jv match {
        case JsObject(fields) =>

          JsSuccess(new DateTime((jv \ "$date").get.toString.toLong))
        case _ => throw new Exception("Unknown JsValue for DateTime: $jv ")
      }
    }
  }

  implicit val dateTimeWrites = new Writes[DateTime] {
    def writes(dt: DateTime): JsValue = {
      Json.toJson(BSONDateTime(dt.getMillis)) // {"$date": millis}
    }
  }

  val dateTimePattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ"
  val externalDateTimeReads = play.api.libs.json.Reads.jodaDateReads(dateTimePattern)
  val externalDateTimeWrites = play.api.libs.json.Writes.jodaDateWrites(dateTimePattern)

  implicit val externalEventRead: Reads[User] = (
    (JsPath \ "_id").readNullable[BSONObjectID] and
    (JsPath \ "email").read[String] and
    (JsPath \ "password").read[String] and
    (JsPath \ "firstName").read[String] and
    (JsPath \ "lastName").read[String] and
    (JsPath \ "inscriptionDate").read[DateTime](externalDateTimeReads) and
    (JsPath \ "lastConnection").read[DateTime](externalDateTimeReads) and
    (JsPath \ "emailConfirmed").read[Boolean] and
    (JsPath \ "active").read[Boolean] and
    (JsPath \ "messageSend").read[Seq[Message]] and
    (JsPath \ "messageReceive").read[Seq[Message]] and
    (JsPath \ "talent").readNullable[Talent]
  )(User.apply _)

  val externalEventWrites: Writes[User] = (
    (JsPath \ "_id").writeNullable[BSONObjectID] and
    (JsPath \ "email").write[String] and
    (JsPath \ "password").write[String] and
    (JsPath \ "firstName").write[String] and
    (JsPath \ "lastName").write[String] and
    (JsPath \ "inscriptionDate").write[DateTime](externalDateTimeWrites) and
    (JsPath \ "lastConnection").write[DateTime](externalDateTimeWrites) and
    (JsPath \ "emailConfirmed").write[Boolean] and
    (JsPath \ "active").write[Boolean] and
    (JsPath \ "messageSend").write[Seq[Message]] and
    (JsPath \ "messageReceive").write[Seq[Message]] and
    (JsPath \ "talent").writeNullable[Talent]
  )(unlift(User.unapply))

  implicit val userFormat = Json.format[User]

  /*  val dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"

    val jodaDateReads = Reads[DateTime](js =>
      js.validate[String].map[DateTime](dtString =>
        DateTime.parse(dtString, DateTimeFormat.forPattern(dateFormat))
      )
    )

    val jodaDateWrites: Writes[DateTime] = new Writes[DateTime] {
      def writes(d: DateTime): JsValue = JsString(d.toString())
    }

    val userReads: Reads[User] = (
      (JsPath \ "name").read[String] and
        (JsPath \ "created").read[DateTime](jodaDateReads)
      )(User.apply _)

    val userWrites: Writes[User] = (
      (JsPath \ "name").write[String] and
        (JsPath \ "created").write[DateTime](jodaDateWrites)
      )(unlift(User.unapply))

    implicit val userFormat: Format[User] = Format(userReads, userWrites)

   import play.api.libs.json._
    import play.api.libs.functional.syntax._

    implicit val locationWrites: Writes[User] = (
      (JsPath \ "lat").write[Double] and
        (JsPath \ "long").write[Double]
      )(unlift(User.unapply))

    implicit val residentReads: Reads[USer] = (
      (JsPath \ "name").read[String](minLength[String](2)) and
        (JsPath \ "age").read[Int](min(0) keepAnd max(150)) and
        (JsPath \ "role").readNullable[String]
      )(User.apply _)*/

}
