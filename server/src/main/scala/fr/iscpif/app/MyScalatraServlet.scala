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

object Server extends Api {
  def hello(a: Int) = a * 3

  def caseClass = MyCaseClass("Hello !")
}

class MyScalatraServlet extends ScalatraServlet {

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
    Await.result(autowire.Macros.route[Api](Server)(
      autowire.Request(Seq(basePath) ++ multiParams("splat").head.split("/"),
        upickle.read[Map[String, String]](request.body))
    ), Duration.Inf)
  }

}