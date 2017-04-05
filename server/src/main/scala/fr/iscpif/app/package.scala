package fr.iscpif.app

import shared.Data._
import at.ait.dme.forcelayout.{Edge, Node}

/**
  * Created by mathieu on 04/04/17.
  */
package object tools {
  lazy val offset = 500
  lazy val scale = 7

  implicit def NodeToTaskData(node: Node): TaskData = TaskData(ID(node.id), node.label, (scale * node.state.pos.x + offset, scale * node.state.pos.y + offset))
  implicit def EdgeToEdgeData(edge: Edge): EdgeData = EdgeData(edge.from, edge.to)

  implicit def SeqOfNodesToSeqOfTaskData(nodes: Seq[Node]): Seq[TaskData] = nodes.map{NodeToTaskData}
  implicit def SeqOfEdgesToSeqOfEdgeData(edges: Seq[Edge]): Seq[EdgeData] = edges.map{EdgeToEdgeData}


  implicit def taskDataToNode(task: TaskData): Node = Node(task.id.id, task.title)
  implicit def taskDataToNodes(tasks: Seq[TaskData]): Seq[Node] = tasks.map {taskDataToNode}
  implicit def edgeDataToEdges(edges: Seq[EdgeData]): Seq[Edge] = edges.map{ e=> Edge(e.source, e.target)}


}
