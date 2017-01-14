package services

import javax.inject.Inject

import models.ApiToken
import play.api.libs.json.{ Format, JsObject }
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ ExecutionContext, Future }

/**
 * Created by marcus on 14/01/17.
 */

trait TokenService extends CRUDService[ApiToken] {

}

class TokenServices @Inject() (reactiveMongoApi: ReactiveMongoApi) extends MongoCRUDService[ApiToken](ApiToken.apiTokenFormat: Format[ApiToken]) with TokenService {

  def collection(implicit ec: ExecutionContext) = reactiveMongoApi.database.map(_.collection[JSONCollection]("user"))

  /* override def find(selector: JsObject)(implicit ec: ExecutionContext): Future[Option[ApiToken]] = {

    collection.flatMap(_.find(selector).one[ApiToken])

  }*/

}

