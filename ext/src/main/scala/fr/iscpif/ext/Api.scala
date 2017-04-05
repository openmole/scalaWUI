package ext

import shared.Data._

trait Api {
  def uuid(): String = java.util.UUID.randomUUID.toString
  def layout(tasks: Seq[TaskData], edges: Seq[EdgeData]): GraphLayout
  def openMOLEWFLayout(): GraphLayout
}