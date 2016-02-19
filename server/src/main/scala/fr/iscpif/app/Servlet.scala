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
import fr.iscpif.ext.Data._

object AutowireServer extends autowire.Server[String, upickle.default.Reader, upickle.default.Writer] {
  def read[Result: upickle.default.Reader](p: String) = upickle.default.read[Result](p)
  def write[Result: upickle.default.Writer](r: Result) = upickle.default.write(r)
}

object ApiImpl extends shared.Api {

  def runningData(i: Int) = RunningData(Seq(RunningEnvironmentData(ExecutionId(), Seq())), Seq(RunningOutputData(ExecutionId(), "nrstrnstie"))))
}

class Servlet extends ScalatraServlet {

  val basePath = "shared"

  get("/") {
    contentType = "text/html"

    tags.html(
      tags.head(
        tags.meta(tags.httpEquiv := "Content-Type", tags.content := "text/html; charset=UTF-8"),
        tags.link(tags.rel := "stylesheet", tags.`type` := "text/css", href := "css/styleWUI.css"),
        tags.script(tags.`type` := "text/javascript", tags.src := "js/client-opt.js"),
        tags.script(tags.`type` := "text/javascript", tags.src := "js/d3.v3.min.js")
      ),
      tags.body(tags.onload := "Client().run();")
    )
  }

  post(s"/$basePath/*") {
    Await.result(AutowireServer.route[shared.Api](ApiImpl)(
      autowire.Core.Request(Seq(basePath) ++ multiParams("splat").head.split("/"),
        upickle.default.read[Map[String, String]](request.body))
    ), Duration.Inf)
  }

}
