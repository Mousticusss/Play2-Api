package controllers

import play.api.mvc._
import javax.inject.Inject

import play.api.i18n.MessagesApi
import play.modules.reactivemongo.ReactiveMongoApi
import services.TokenServices

class Application @Inject() (val messagesApi: MessagesApi,val tokenServices: TokenServices, val reactiveMongoApi: ReactiveMongoApi) extends api.ApiController {

  def test = ApiAction { implicit request =>

    ok("The API is ready")

  }
  /*def index = Action {
    /**
     * change the template here to use a different way of compilation and loading of the ts ng2 assets.app.
     * index()  :    does no ts compilation in advance. the ts files are download by the browser and compiled there to js.
     * index1() :    compiles the ts files to individual js files. Systemjs loads the individual files.
     * index2() :    add the option -DtsCompileMode=stage to your sbt task . F.i. 'sbt ~run -DtsCompileMode=stage' this will produce the assets.app as one single js file.
     */
    Ok(views.html.index()); ;

  }
*/
  // Auxiliar to check the FakeDB information. It's only for testing purpose. You should remove it.
  /*def fakeDB = Action { implicit request =>
    Ok(views.html.fakeDB())
  }*/

}
