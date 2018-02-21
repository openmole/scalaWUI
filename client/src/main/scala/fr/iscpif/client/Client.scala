package client

import java.nio.ByteBuffer

import org.scalajs.dom

import scala.concurrent.Future
import rx._

import scala.scalajs.js.annotation.JSExportTopLevel
import boopickle.Default._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js.typedarray.{ArrayBuffer, TypedArrayBuffer}


object Client {

  val helloValue = Var(0)
  val caseClassValue = Var("empty")

  @JSExportTopLevel("run")
  def run() {
    val nodes = Seq(
      Graph.task("0", "one", 400, 600),
      Graph.task("1", "two", 1000, 600),
      Graph.task("2", "three", 400, 100),
      Graph.task("3", "four", 1000, 100),
      Graph.task("4", "five", 105, 60)
    )
    val edges = Seq(
      Graph.edge(nodes(0), nodes(1)),
      Graph.edge(nodes(0), nodes(2)),
      Graph.edge(nodes(3), nodes(1)),
      Graph.edge(nodes(3), nodes(2)))
    val window = new Window(nodes, edges)
  }
}

object Post extends autowire.Client[ByteBuffer, Pickler, Pickler] {

  override def doCall(req: Request): Future[ByteBuffer] = {
    dom.ext.Ajax.post(
      url = req.path.mkString("/"),
      data = Pickle.intoBytes(req.args),
      responseType = "arraybuffer",
      headers = Map("Content-Type" -> "application/octet-stream")
    ).map(r => TypedArrayBuffer.wrap(r.response.asInstanceOf[ArrayBuffer]))
  }

  override def read[Result: Pickler](p: ByteBuffer) = Unpickle[Result].fromBytes(p)

  override def write[Result: Pickler](r: Result) = Pickle.intoBytes(r)

}
