package controllers

import api.ApiError._
import api.JsonCombinators._
import models.{Folder, Task}

import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject

import play.api.i18n.MessagesApi
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import javax.inject.Inject

import scala.concurrent.Future
import play.api.Logger
import play.api.mvc.{Action, Controller}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.functional.syntax._
import reactivemongo.api.Cursor
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.play.json._
import collection._
import play.api.libs.json.Json
import services.TokenServices
// BSON-JSON conversions/collection
import reactivemongo.play.json._

class Folders @Inject() (val reactiveMongoApi: ReactiveMongoApi, val messagesApi: MessagesApi,val tokenServices: TokenServices) extends api.ApiController {

  def list(sort: Option[String], p: Int, s: Int) = SecuredApiAction { implicit request =>
    sortedPage(sort, Folder.sortingFields, default = "order") { sortingFields =>
      Folder.page(1, sortingFields, p, s)
    }
  }

  // Returns the Location header, but not the folder information within the content body.
  def insert = SecuredApiActionWithBody { implicit request =>
    readFromRequest[Folder] { folder =>
      Folder.insert(1, folder.name).flatMap {
        //     case (id, newFolder) => created(Api.locationHeader(routes.Folders.info(id)))
        case (id, newTask) => created(newTask)
      }
    }
  }

  // protected def collection =
  // db.collection[JSONCollection]("posts")
  def collection = reactiveMongoApi.database.map(_.collection[JSONCollection]("posts"))

  def insert2 = SecuredApiActionWithBody { implicit request =>
    readFromRequest[Folder] { folder =>

      val jFolder = Folder(
        1,

        1,
        1,
        folder.name
      )

      for {
        talentCol <- collection
        lastError <- talentCol.insert(jFolder)
      } yield Ok("Mongo LastError: %s".format(lastError))

      created(jFolder)

      //  Ok("Mongo LastError: %s".format(lastError))

      //  created(lastErrror.originalDocument);
    }
  }

  /*

  def insert = SecuredApiActionWithBody { implicit request =>
    readFromRequest[Folder] { folder =>

      val json =
        collection.insert(Folder(
          1,

          request.userId,
          1,
          folder.name


        ))

      created(folder.name);
    }
  }
*/

  def info(id: Long) = SecuredApiAction { implicit request =>
    maybeItem(Folder.findById(id))
  }

  def update(id: Long) = SecuredApiActionWithBody { implicit request =>
    readFromRequest[Folder] { folder =>
      Folder.basicUpdate(id, folder.name).flatMap { isOk =>
        if (isOk) noContent() else errorInternal
      }
    }
  }

  def updateOrder(id: Long, newOrder: Int) = SecuredApiAction { implicit request =>
    Folder.updateOrder(id, newOrder).flatMap { isOk =>
      if (isOk) noContent() else errorInternal
    }
  }

  def delete(id: Long) = SecuredApiAction { implicit request =>
    Folder.delete(id).flatMap { _ =>
      noContent()
    }
  }

}