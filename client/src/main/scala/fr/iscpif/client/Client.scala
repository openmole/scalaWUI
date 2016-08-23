package client

import org.scalajs.dom
import scala.concurrent.Future
import rx._
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow

@JSExport("Client")
object Client {

  val helloValue = Var(0)
  val caseClassValue = Var("empty")

  @JSExport
  def run() {
      val nodes = scala.Array(
        Graph.task("1", "one", 400, 600),
        Graph.task("2", "two", 1000, 600),
        Graph.task("3", "three", 400, 100),
        Graph.task("4", "four", 1000, 100),
        Graph.task("5", "five", 105, 60)
      )
      val edges = scala.Array(
        Graph.edge(nodes(0), nodes(1)),
        Graph.edge(nodes(0), nodes(2)),
        Graph.edge(nodes(3), nodes(1)),
        Graph.edge(nodes(3), nodes(2)))
      val window = new Window(nodes, edges)
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
