package fr.iscpif.app

import java.util.UUID

import org.scalatra._
import scala.concurrent.ExecutionContext.Implicits.global
import upickle._
import autowire._
import shared._
import upickle._
import scala.concurrent.duration._
import scala.concurrent.Await
import scalatags.Text.all._
import scalatags.Text.{all => tags}

object AutowireServer extends autowire.Server[String, upickle.Reader, upickle.Writer]{
 def read[Result: upickle.Reader](p: String) = upickle.read[Result](p)
 def write[Result: upickle.Writer](r: Result) = upickle.write(r)
}

object ApiImpl extends Api {
  def hello(a: Int) = a * 3

  def caseClass = MyCaseClass("Hello !")
}

class Server extends ScalatraServlet {

  val basePath = "shared"

  get("/") {
    contentType = "text/html"

    tags.html(
      tags.head(
        tags.meta(tags.httpEquiv := "Content-Type", tags.content := "text/html; charset=UTF-8"),
        tags.script(tags.`type` := "text/javascript", tags.src := "js/client-fastopt.js"),
        tags.script(tags.`type` := "text/javascript", tags.src := "js/client-opt.js")
      ),
      tags.body(tags.onload := "Client().run();")
    )
  }

  post(s"/$basePath/*") {
    Await.result(AutowireServer.route[Api](ApiImpl)(
      autowire.Core.Request(Seq(basePath) ++ multiParams("splat").head.split("/"),
        upickle.read[Map[String, String]](request.body))
    ), Duration.Inf)
  }

}