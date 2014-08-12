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
    val submitButton1 = button("Click me")(
      cursor := "pointer",
      onclick := { () =>
        Post[Api](_.hello(5)).foreach { i =>
          helloValue() = helloValue() + i
        }
      }
    ).render

    val submitButton2 = button("Click me")(
      cursor := "pointer",
      onclick := { () =>
        Post[Api](_.caseClass).foreach { s =>
          caseClassValue() = s.hello
        }
        false
      }
    ).render

      dom.document.body.appendChild(submitButton1)
      dom.document.body.appendChild(submitButton2)

    Rx {
      dom.document.body.appendChild(h1(helloValue).render)
      dom.document.body.appendChild(h1(caseClassValue).render)
    }
  }

}

object Post extends autowire.Client[Web] {

  override def callRequest(req: Request): Future[String] = {
    val url = req.path.mkString("/")
    dom.extensions.Ajax.post(
      url = "http://localhost:8080/" + url,
      data = upickle.write(req.args)
    ).map {
      _.responseText
    }
  }
}
