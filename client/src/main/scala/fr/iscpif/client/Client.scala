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
    val nodes = Seq(
      TaskData(title = "one"),
      TaskData(title = "two"),
      TaskData(title = "three"),
      TaskData(title = "four"),
      TaskData(title = "five")
    )

    val edges = Seq(
      EdgeData(nodes(0), nodes(1)),
      EdgeData(nodes(0), nodes(2)),
      EdgeData(nodes(3), nodes(1)),
      EdgeData(nodes(3), nodes(2))
    )

    new Window(nodes, edges)
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
