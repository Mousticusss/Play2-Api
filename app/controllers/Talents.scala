package controllers

import models.Folder
import play.api.Logger
import play.api.libs.json.{ JsError, JsSuccess }
import reactivemongo.api.commands.WriteResult
import reactivemongo.play.json.collection.JSONCollection
import services.TokenServices

import scala.concurrent.Future
import scala.util.Failure
/**
 * Created by marcus on 21/09/16.
 */

import api.ApiError._
import api.JsonCombinators._
import models.Talent
import models.Task
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject

import play.api.i18n.MessagesApi
import play.modules.reactivemongo.{ MongoController, ReactiveMongoApi, ReactiveMongoComponents }
import reactivemongo.bson.{ BSONDocument, BSONObjectID }
import javax.inject.Inject

class Talents @Inject() (val messagesApi: MessagesApi, val reactiveMongoApi: ReactiveMongoApi, val tokenServices: TokenServices) extends api.ApiController {
  /*

  /*   email: String,
  password: String,
  name: String,
  emailConfirmed: Boolean,
  active: Boolean*/

  //protected def collection =
  //  db.collection[JSONCollection]("posts")
  def collection = reactiveMongoApi.database.map(_.collection[JSONCollection]("talents"))

  def createPerson(person: Talent): Future[Unit] =
    collection.flatMap(_.insert(person).map(_ => {}))

  def insert = SecuredApiActionWithBody { implicit request =>
    readFromRequest[Talent] { talent =>

      val Bson = Talent(talent.name, talent.email, talent.password, talent.emailConfirmed, talent.active)
      collection.flatMap(_.insert(Bson).map(_ => {}))

      /*   for {
        talentCol <- collection
        lastError <- talentCol.insert(Bson)
      } yield
        {
          lastError.hasErrors match {
            case true=> created(Bson)
            case false => created(Bson)

          }


        }

*/
      created(Bson)

      //  Ok("Mongo LastError: %s".format(lastError))

      //  created(lastErrror.originalDocument);
    }
  }

  /*

  def list(folderId: Long, q: Option[String], done: Option[Boolean], sort: Option[String], p: Int, s: Int) =
    SecuredApiAction { implicit request =>
      sortedPage(sort, Task.sortingFields, default = "order") { sortingFields =>
        Task.page(folderId, q, done, sortingFields, p, s)
      }
    }


  def insert(folderId: Long) = SecuredApiActionWithBody { implicit request =>
    readFromRequest[Task] { task =>
      Task.insert(folderId, task.text, new Date(), task.deadline).flatMap {
        case (id, newTask) => created(newTask)
      }
    }
  }






*/
*/

}
