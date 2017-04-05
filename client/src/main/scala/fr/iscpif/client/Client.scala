package client

import org.scalajs.dom
import scala.concurrent.Future
import rx._
import shared.Data._
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow


@JSExport("Client")
object Client {

  @JSExport
  def run() = {
    val nodes: Seq[TaskData] = for (t <- 0 to 100) yield {
      TaskData(title = t.toString)
    }

    val r = scala.util.Random
    val edges = for (e <- 0 to 100) yield {
      EdgeData(nodes(r.nextInt(10)), nodes(r.nextInt(100)))
    }

    Window.render(nodes, edges)
  }

  @JSExport
  def runOpenMOLEWF() = Window.renderOpenMOLEWF

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
