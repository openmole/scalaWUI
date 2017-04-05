package fr.iscpif.app

import org.scalatra._

import scala.concurrent.ExecutionContext.Implicits.global
import at.ait.dme.forcelayout.{Edge, Node, SpringGraph}
import fr.iscpif.app.tools._
import org.openmole.core.context.Val
import org.openmole.core.dsl._
import org.openmole.plugin.task.scala._
import org.openmole.plugin.hook.display._
import org.openmole.plugin.method.directsampling._
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

  def openMOLEWFLayout: GraphLayout = {
    val seed = Val[Long]
    val pi = Val[Double]
    val piAvg = Val[Double]

    val model =
      ScalaTask(
        """
          |val pi = 3.14
          |""".stripMargin) set(
        inputs += seed,
        outputs += pi
      )

    val average =
      ScalaTask("val piAvg = pi.sum / pi.size") set(
        inputs += pi.toArray,
        outputs += piAvg
      )

    val exploration =
      Replication(
        evaluation = model hook ToStringHook(),
        seed = seed,
        replications = 100,
        aggregation = average hook ToStringHook()
      )

    val capsules = exploration.capsules.map { c =>
      c -> TaskData(title = c.task.name.getOrElse(""))
    }.toMap

    val transitions = exploration.transitions.map { t =>
      EdgeData(capsules(t.start), capsules(t.end.capsule))
    }.toSeq

    layout(capsules.values.toSeq, transitions)

  }

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
      tags.body(tags.onload := "Client().runOpenMOLEWF();")
    )
  }

  post(s"/$basePath/*") {
    Await.result(AutowireServer.route[ext.Api](ApiImpl)(
      autowire.Core.Request(Seq(basePath) ++ multiParams("splat").head.split("/"),
        upickle.default.read[Map[String, String]](request.body))
    ), Duration.Inf)
  }

}
