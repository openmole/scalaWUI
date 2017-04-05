package fr.iscpif.app

import java.util.UUID

import org.scalatra._

import scala.concurrent.ExecutionContext.Implicits.global
import upickle.default
import autowire._
import upickle._
import at.ait.dme.forcelayout.{Edge, Node, SpringGraph}
import fr.iscpif.app.tools._
import ext._
import shared.Data._
import scala.concurrent.duration._
import scala.concurrent.Await
import scalatags.Text.all._
import scalatags.Text.{all => tags}

object AutowireServer extends autowire.Server[String, upickle.default.Reader, upickle.default.Writer] {
  def read[Result: upickle.default.Reader](p: String) = upickle.default.read[Result](p)
  def write[Result: upickle.default.Writer](r: Result) = upickle.default.write(r)
}

object ApiImpl extends ext.Api {

  def layout(tasks: Seq[TaskData], edges: Seq[EdgeData]): GraphLayout = {
    val graphNodes: Seq[Node] = tasks
    val graphEdges: Seq[Edge] = edges

    val graph = new SpringGraph(graphNodes, graphEdges)

    graph.doLayout()

    GraphLayout(graph.nodes, graph.edges)
  }
}

class Servlet extends ScalatraServlet {

  val basePath = "ext"

  get("/") {
    contentType = "text/html"

    tags.html(
      tags.head(
        tags.meta(tags.httpEquiv := "Content-Type", tags.content := "text/html; charset=UTF-8"),
        tags.link(tags.rel := "stylesheet", tags.`type` := "text/css", href := "css/styleWUI.css"),
        tags.script(tags.`type` := "text/javascript", tags.src := "js/client-opt.js"),
        tags.script(tags.`type` := "text/javascript", tags.src := "js/client-jsdeps.min.js")
      ),
      tags.body(tags.onload := "Client().run();")
    )
  }

  post(s"/$basePath/*") {
    Await.result(AutowireServer.route[ext.Api](ApiImpl)(
      autowire.Core.Request(Seq(basePath) ++ multiParams("splat").head.split("/"),
        upickle.default.read[Map[String, String]](request.body))
    ), Duration.Inf)
  }

}
