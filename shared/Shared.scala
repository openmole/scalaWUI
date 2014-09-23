package shared

import scala.scalajs.js.annotation.JSExport


@JSExport
case class MyCaseClass(hello: String)

trait Api {
  def hello(a: Int): Int
  def caseClass(): MyCaseClass
  def uuid(): String = java.util.UUID.randomUUID.toString
}