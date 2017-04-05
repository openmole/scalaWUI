package shared

import java.util.UUID

// DEFINE SOME CASE CLASS TO STORE TASK AND EDGE STRUCTURES

object Data {
  case class ID(id: String)

  def uuid = ID(UUID.randomUUID.toString.split("-").head)

  case class TaskData(id: ID = uuid,
                      title: String = "",
                      location: (Double, Double) = (0.0, 0.0))

  case class EdgeData(val source: TaskData,
                      val target: TaskData)

  case class GraphLayout(tasks: Seq[TaskData], edges: Seq[EdgeData])

}