package client

import java.util.UUID

import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import scala.concurrent.Future
import scalatags.JsDom._
import all._
import tags2.section
import rx._
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import org.scalajs.dom.extensions.Ajax
import scala.Some
import shared._
import upickle._
import autowire._
import JsRxTags._

@JSExport
object Client {

  val helloValue = Var(0)
  val caseClassValue = Var("empty")

  @JSExport
  def run() {
   /* val submitButton1 = button("Click me")(
      cursor := "pointer",
      onclick := { () =>
        Post[Api].hello(5).call().foreach { i =>
          helloValue() = helloValue() + i
        }
      }
    ).render

    val submitButton2 = button("Click me")(
      cursor := "pointer",
      onclick := { () =>
        Post[Api].caseClass.call().foreach { s =>
          caseClassValue() = s.hello
        }
        false
      }
    ).render

    dom.document.body.appendChild(submitButton1)
    dom.document.body.appendChild(submitButton2)

    Rx {
      println("RX " + helloValue)
      dom.document.body.appendChild(h1(helloValue).render)
      dom.document.body.appendChild(h1(caseClassValue).render)
    }
  }*/
    val nodes = scala.Array(
      new Task("1",Var("one"),Var((400,600))),
      new Task("2",Var("two"),Var((1000,600))),
      new Task("3",Var("three"),Var((400,100))),
      new Task("4",Var("four"),Var((1000,100))),
      new Task("5",Var("five"),Var((105,60)))
    )
    val edges = scala.Array(new Edge(Var(nodes(0)),Var(nodes(1))),new Edge(Var(nodes(0)),Var(nodes(2))),new Edge(Var(nodes(3)),Var(nodes(1))))
    val window = new Window(nodes,edges)
  }

}

object Post extends autowire.Client[String, upickle.Reader, upickle.Writer] {

  override def doCall(req: Request): Future[String] = {
    val url = req.path.mkString("/")
    dom.extensions.Ajax.post(
      url = "http://localhost:8080/" + url,
      data = upickle.write(req.args)
    ).map {
      _.responseText
    }
  }

  def read[Result: upickle.Reader](p: String) = upickle.read[Result](p)

  def write[Result: upickle.Writer](r: Result) = upickle.write(r)
}
