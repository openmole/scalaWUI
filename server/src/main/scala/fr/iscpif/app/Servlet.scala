package org.openmole.app

import java.nio.ByteBuffer

import org.scalatra._

import scala.concurrent.ExecutionContext.Implicits.global
import boopickle.Default._

import scala.concurrent.duration._
import scala.concurrent.Await
import scalatags.Text.all._
import scalatags.Text.{all => tags}

object AutowireServer extends autowire.Server[ByteBuffer, Pickler, Pickler] {
  override def read[R: Pickler](p: ByteBuffer) = Unpickle[R].fromBytes(p)

  override def write[R: Pickler](r: R) = Pickle.intoBytes(r)
}


class Servlet extends ScalatraServlet {

  val basePath = "shared"

  get("/") {
    contentType = "text/html"

    tags.html(
      tags.head(
        tags.meta(tags.httpEquiv := "Content-Type", tags.content := "text/html; charset=UTF-8"),
        tags.link(tags.rel := "stylesheet", tags.`type` := "text/css", href := "css/styleWUI.css"),
        tags.link(tags.rel := "stylesheet", tags.`type` := "text/css", href := "css/deps.css"),
        tags.script(tags.`type` := "text/javascript", tags.src := "js/demo.js")
      ),
      body(
      tags.div(id := "scalaWUI-content"),
      tags.script("scalaWui.graph();")
      )
    )
  }

  post(s"/$basePath/*") {
    val req = Await.result({
      val is = request.getInputStream
      val bytes: Array[Byte] = Iterator.continually(is.read()).takeWhile(_ != -1).map(_.asInstanceOf[Byte]).toArray[Byte]
      val bb = ByteBuffer.wrap(bytes)
      AutowireServer.route[shared.Api](ApiImpl)(
        autowire.Core.Request(
          basePath.split("/").toSeq ++ multiParams("splat").head.split("/"),
          Unpickle[Map[String, ByteBuffer]].fromBytes(bb)
        )
      )
    },
      Duration.Inf
    )

    val data = Array.ofDim[Byte](req.remaining)
    req.get(data)
    Ok(data)
  }

}
