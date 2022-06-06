package client

import java.nio.ByteBuffer
import org.scalajs.dom

import scala.concurrent.Future
import org.scalajs

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js.typedarray.{ArrayBuffer, TypedArrayBuffer}
import com.raquo.laminar.api.L._

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportTopLevel (name="scalaWui")
@JSExportAll
object App {

  //def main(args: Array[String]): Unit = {

  def graph() = {
    val nodes = Seq(
      Graph.task("one", 400, 600),
      Graph.task("two", 1000, 600),
      Graph.task("three", 400, 100),
      Graph.task("four", 1000, 100),
      Graph.task("five", 105, 60)
    )
    val edges = Seq(
      Graph.edge(nodes(0), nodes(1)),
      Graph.edge(nodes(0), nodes(2)),
      Graph.edge(nodes(3), nodes(1)),
      Graph.edge(nodes(3), nodes(2)))

    val graphCreator = new GraphCreator(nodes, edges)

    val containerNode = dom.document.querySelector("#scalaWUI-content")

    render(containerNode, graphCreator.svgNode)
  }
}

//object Post extends autowire.Client[ByteBuffer, Pickler, Pickler] {
//
//  override def doCall(req: Request): Future[ByteBuffer] = {
//    dom.ext.Ajax.post(
//      url = req.path.mkString("/"),
//      data = Pickle.intoBytes(req.args),
//      responseType = "arraybuffer",
//      headers = Map("Content-Type" -> "application/octet-stream")
//    ).map(r => TypedArrayBuffer.wrap(r.response.asInstanceOf[ArrayBuffer]))
//  }
//
//  override def read[Result: Pickler](p: ByteBuffer) = Unpickle[Result].fromBytes(p)
//
//  override def write[Result: Pickler](r: Result) = Pickle.intoBytes(r)
//
//}
