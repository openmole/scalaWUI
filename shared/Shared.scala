package shared

import scala.annotation.ClassfileAnnotation
import scala.scalajs.js.annotation.JSExport

class Web extends ClassfileAnnotation

@Web
@JSExport
case class MyCaseClass(hello: String)


@Web
trait Api {
  def hello(a: Int): Int
  def caseClass(): MyCaseClass
}