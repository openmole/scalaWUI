package client

import fr.iscpif.client.{Test, BootstrapDemo}
import fr.iscpif.scaladget.stylesheet.bootstrap
import org.scalajs.dom
import scala.concurrent.Future
import scalatags.JsDom._
import all._
import scalatags.generic.StylePair
import tags2.section
import rx._
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import fr.iscpif.scaladget.api.{BootstrapTags ⇒ bs}
import bs._
import shared._
import upickle._
import autowire._
import client.JsRxTags._

@JSExport("Client")
object Client {


  @JSExport
  def run() {
    dom.document.body.appendChild(BootstrapDemo.build)
    dom.document.body.appendChild(Test.build)
  }

}

object Post extends autowire.Client[String, upickle.default.Reader, upickle.default.Writer] {

  override def doCall(req: Request): Future[String] = {
    val url = req.path.mkString("/")
    dom.ext.Ajax.post(
      url = "http://localhost:8080/" + url,
      data = upickle.default.write(req.args)
    ).map {
      _.responseText
    }
  }

  def read[Result: upickle.default.Reader](p: String) = upickle.default.read[Result](p)

  def write[Result: upickle.default.Writer](r: Result) = upickle.default.write(r)
}
