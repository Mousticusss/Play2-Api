package controllers

import api._
import api.ApiError._
import api.JsonCombinators._
import models.Task
import play.api.mvc._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject

import play.api.i18n.MessagesApi
import java.util.Date

import play.modules.reactivemongo.{ MongoController, ReactiveMongoApi, ReactiveMongoComponents }
import reactivemongo.bson.{ BSONDocument, BSONObjectID }
import reactivemongo.play.json.collection.JSONCollection
import services.TokenServices

class Tasks @Inject() (
    val reactiveMongoApi: ReactiveMongoApi, val messagesApi: MessagesApi, val tokenServices: TokenServices
) extends api.ApiController with MongoController with ReactiveMongoComponents {

  def list(folderId: Long, q: Option[String], done: Option[Boolean], sort: Option[String], p: Int, s: Int) = SecuredApiAction { implicit request =>
    sortedPage(sort, Task.sortingFields, default = "order") { sortingFields =>
      Task.page(folderId, q, done, sortingFields, p, s)
    }
  }

  /* protected def collection =
    reactiveMongoApi.db.collection[JSONCollection]("posts")

  def create  = Action.async(BodyParsers.parse.json) { implicit request =>
    val username = (request.body \ Username).as[String]
    val text = (request.body \ Text).as[String]
    val avatar = (request.body \ Avatar).as[String]
    val document= BSONDocument(
      Text -> text,
      Username -> username,
      Avatar -> avatar,
      Favorite -> false
    )
    collection.update(BSONDocument("_id" -> document.get("_id").getOrElse(BSONObjectID.generate)), document, upsert = true)
    postRepo.save(BSONDocument(
      Text -> text,
      Username -> username,
      Avatar -> avatar,
      Favorite -> false
    )).map(le => Redirect(routes.Posts.list()))
  }
*/

  // Returns the task information within the content body, but not the Location header.
  def insert(folderId: Long) = SecuredApiActionWithBody { implicit request =>
    readFromRequest[Task] { task =>
      Task.insert(folderId, task.text, new Date(), task.deadline).flatMap {
        case (id, newTask) => created(newTask)
      }
    }
  }

  def info(id: Long) = SecuredApiAction { implicit request =>
    maybeItem(Task.findById(id))
  }

  def update(id: Long) = SecuredApiActionWithBody { implicit request =>
    readFromRequest[Task] { task =>
      Task.basicUpdate(id, task.text, task.deadline).flatMap { isOk =>
        if (isOk) noContent() else errorInternal
      }
    }
  }

  def updateOrder(id: Long, newOrder: Int) = SecuredApiAction { implicit request =>
    Task.updateOrder(id, newOrder).flatMap { isOk =>
      if (isOk) noContent() else errorInternal
    }
  }

  def updateFolder(id: Long, folderId: Long) = SecuredApiAction { implicit request =>
    Task.updateFolder(id, folderId).flatMap { isOk =>
      if (isOk) noContent() else errorInternal
    }
  }

  def updateDone(id: Long, done: Boolean) = SecuredApiAction { implicit request =>
    Task.updateDone(id, done).flatMap { isOk =>
      if (isOk) noContent() else errorInternal
    }
  }

  def delete(id: Long) = SecuredApiAction { implicit request =>
    Task.delete(id).flatMap { _ =>
      noContent()
    }
  }

}