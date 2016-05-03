package fr.iscpif.app

import org.scalatra._
import scala.concurrent.ExecutionContext.Implicits.global
import upickle.default
import autowire._
import shared._
import upickle._
import scala.concurrent.duration._
import scala.concurrent.Await
import scalatags.Text.all._
import scalatags.Text.{all => tags}

object AutowireServer extends autowire.Server[String, upickle.default.Reader, upickle.default.Writer] {
  def read[Result: upickle.default.Reader](p: String) = upickle.default.read[Result](p)
  def write[Result: upickle.default.Writer](r: Result) = upickle.default.write(r)
}

object ApiImpl extends shared.Api {
}

class Servlet extends ScalatraServlet {

  val basePath = "shared"

  get("/") {
    contentType = "text/html"

    tags.html(
      tags.head(
        tags.meta(tags.httpEquiv := "Content-Type", tags.content := "text/html; charset=UTF-8"),
        tags.link(tags.rel := "stylesheet", tags.`type` := "text/css", href := "css/styleWUI.css"),
        tags.link(tags.rel := "stylesheet", tags.`type` := "text/css", href := "css/taglibrary.css"),
        tags.link(tags.rel := "stylesheet", tags.`type` := "text/css", href := "css/bootstrap.min.css"),
        tags.script(tags.`type` := "text/javascript", tags.src := "js/client-jsdeps.min.js"),
        tags.script(tags.`type` := "text/javascript", tags.src := "js/client-opt.js")
      ),
      tags.body(`class` := "uu", tags.onload := "Client().run();")
    )
  }

  post(s"/$basePath/*") {
    Await.result(AutowireServer.route[shared.Api](ApiImpl)(
      autowire.Core.Request(Seq(basePath) ++ multiParams("splat").head.split("/"),
        upickle.default.read[Map[String, String]](request.body))
    ), Duration.Inf)
  }

}
