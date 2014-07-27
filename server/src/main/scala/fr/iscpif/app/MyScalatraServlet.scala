package fr.iscpif.app

import org.scalatra._
import scala.concurrent.ExecutionContext.Implicits.global
import autowire._
import shared._
import scala.concurrent.duration._
import scala.concurrent.Await

object Server extends Api {
  def hello(a: Int) = a * 3
}

class MyScalatraServlet extends ServertestStack {

  get("/") {
    contentType = "text/html"
    jade("/default.jade")
  }

  post("/shared/Api/hello") {
    Await.result(autowire.Macros.route[Web](Server)(
      autowire.Request(Seq("shared", "Api", "hello"),
        upickle.read[Map[String, String]](request.body))
    ),100.seconds)

  }
}
