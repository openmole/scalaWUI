package client

/*
 * Copyright (C) 22/09/14 // mathieu.leclaire@openmole.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


import scaladget.bootstrapnative.bsn._
import com.raquo.laminar.api.L._
import com.raquo.laminar.api.L.svg
import com.raquo.domtypes.jsdom.defs.events.TypedTargetMouseEvent
import org.scalajs
import org.scalajs.dom.{KeyboardEvent, raw}


import fr.iscpif.client.APIClient
import scala.scalajs.js.Dynamic

trait Selectable {
  val selected: Var[Boolean] = Var(false)
}

// DEFINE SOME CASE CLASS TO STORE TASK AND EDGE STRUCTURES
object Graph {

  case class Task(title: Var[String] = Var(""),
                  location: Var[(Double, Double)] = Var((0.0, 0.0))) extends Selectable

  class Edge(val source: Var[Task],
             val target: Var[Task]) extends Selectable

  def task(title: String, x: Double, y: Double) = Task(Var(title), Var((x, y)))

  def edge(source: Task, target: Task) = new Edge(Var(source), Var(target))
}

import Graph._

class GraphCreator(_tasks: Seq[Task], _edges: Seq[Edge]) {

  val SELECTED: String = "selected"
  val CIRCLE: String = "conceptG"
  val LINK: String = "link"
  val LINK_DRAGLINE: String = "link dragline"
  val HIDDEN: String = "hidden"
  val DELETE_KEY = 46
  val NODE_RADIUS = 50
  val END_ARROW: String = "end-arrow"
  val URL_END_ARROW: String = s"url(#${END_ARROW})"
  val MARK_END_ARROW: String = "mark-end-arrow"
  val URL_MARK_END_ARROW: String = s"url(#${MARK_END_ARROW})"

  // DEFINE A SVG ELEMENT TO DISPLAY A PHANTOM LINK WHILE DRAGGING A LINK FROM ONE TASK TO ANOTHER
  class DragLine {
    private val m: Var[(Int, Int)] = Var((0, 0))
    private val l: Var[(Int, Int)] = Var((0, 0))
    val dragging = Var(false)

    def move(x: Int, y: Int) = {
      dragging.set(true)
      m.set((x, y))
      this
    }

    def line(x: Int, y: Int) = {
      dragging.set(true)
      l.set((x, y))
      this
    }

    val render = {
      svg.path(
        svg.d <-- m.signal.combineWith(l.signal).map { case (mx, my, lx, ly) => s"M $mx $my L $lx $ly" },
        svg.markerEnd := URL_MARK_END_ARROW,
        svg.cls := LINK_DRAGLINE,
        svg.cls.toggle(HIDDEN) <-- dragging.signal.map {
          !_
        }
      )
    }
  }

  implicit def dynamicToString(d: Dynamic): String = d.asInstanceOf[String]

  implicit def dynamicToBoolean(d: Dynamic): Boolean = d.asInstanceOf[Boolean]

  // SVG DEFINITIONS
  lazy val dragLine = new DragLine

  val tasks: Var[Seq[Task]] = Var(Seq())
  _tasks.map {
    addTask
  }

  val edges: Var[Seq[Edge]] = Var(Seq())
  _edges.map { e =>
    addEdge(edge(e.source.now, e.target.now))
  }

  val svgG = svg.g(
    dragLine.render,
    svg.g(
      children <-- tasks.signal.combineWith(edges).map { case (t, e) =>
        e.map(link) ++ t.map(circle)
      }
    )
  )

  val mouseDownTask: Var[Option[Task]] = Var(None)
  val dragging: Var[Option[DragLine]] = Var(None)


  def mousemove(me: TypedTargetMouseEvent[raw.Element]) = {
    Seq(mouseDownTask.now).flatten.map {
      t ⇒
        val x = me.clientX
        val y = me.clientY
        if (me.shiftKey) {
          dragLine.move(t.location.now._1.toInt, t.location.now._2.toInt).line(x.toInt, y.toInt)
        }
        else {
          t.location.set((x, y))
        }
    }
  }

  def mouseup(me: TypedTargetMouseEvent[raw.Element]) = {
    import scala.concurrent.ExecutionContext.Implicits.global
    // Hide the drag line
    if (me.shiftKey && !dragLine.dragging.now) {
      val (x, y) = (me.clientX, me.clientY)
      APIClient.uuid().future.onComplete { i =>
        println("I " + i.get)
        addTask(task(i.get, x, y))
      }
    }
    mouseDownTask.set(None)
    dragLine.dragging.set(false)
  }

  // ARROW MARKERS FOR GRAPH LINKS
  def arrow = svg.marker(
    svg.viewBox := "0 -5 10 10",
    svg.markerWidth := "3.5",
    svg.markerHeight := "3.5",
    svg.orient := "auto",
    svg.refX := "32"
  )

  def endArrowMarker = arrow.amend(
    svg.idAttr := END_ARROW,
    svg.refX := "32",
    scaladget.svg.path.start(0, -5).l(10, 0).l(0, 5).render
  )

  def markEndArrow = arrow.amend(
    svg.idAttr := MARK_END_ARROW,
    svg.refX := "7",
    scaladget.svg.path.start(0, -5).l(10, 0).l(0, 5).render
  )

  val defs = svg.defs(
    endArrowMarker,
    markEndArrow
  )

  // RETURN A SVG CIRCLE, WHICH CAN BE SELECTED (ON CLICK), MOVED OR DELETED (DEL KEY)
  def circle(task: Task) = {
    val element: SvgElement =
      svg.g(
        svg.cls := CIRCLE,
        svg.cls.toggle((SELECTED)) <-- task.selected.signal,
        svg.transform <-- task.location.signal.map {
          l =>
            s"translate(${
              l._1
            },${
              l._2
            })"
        },
        svg.circle(svg.r := NODE_RADIUS.toString)
      )


    val gCircle = svg.g(
      element,
      onMouseDown --> {
        me =>
          mouseDownTask.set(Some(task))
          me.stopPropagation()
          unselectTasks
          unselectEdges
          task.selected.update(!_)
      },
      onMouseUp --> {
        _ =>
          Seq(mouseDownTask.now).flatten.map {
            mdt ⇒
              if (task != mdt) {
                addEdge(edge(mdt, task))
              }
          }
      }
    )
    gCircle
  }

  // DEFINE A LINK, WHICH CAN BE SELECTED AND REMOVED (DEL KEY)
  def link(edge: Edge) =
    svg.g(
      svg.path(
        svg.d <-- edge.source.signal.map(_.location).combineWith(edge.target.signal.map(_.location)).map {
          case (source, target) =>
            source.signal.combineWith(target.signal).map {
              case (sx, sy, tx, ty) =>
                s"M $sx $sy L $tx $ty"
            }
        }.flatten,
        svg.markerEnd := URL_END_ARROW,
        svg.cls := LINK,
        svg.cls.toggle(SELECTED) <-- edge.selected.signal,
        onMouseDown --> {
          _ =>
            unselectTasks
            unselectEdges
            edge.selected.update(!_)
        }
      )
    )

  val svgNode = svg.svg(
    svg.width := "2500",
    svg.height := "2500",
    defs,
    svgG,
    onMouseMove --> (me => mousemove(me)),
    onMouseUp --> (me => mouseup(me))
  )

  // DEAL WITH DEL KEY ACTION
  scalajs.dom.document.onkeydown = (e: KeyboardEvent) => {
    e.keyCode match {
      case DELETE_KEY ⇒
        tasks.now.filter(t ⇒ t.selected.now).map(t ⇒ removeTask(t))
        edges.now.filter(e ⇒ e.selected.now).map(e ⇒ removeEdge(e))
      case _ ⇒
    }
  }

  // ADD, SELECT AND REMOVE ITEMS
  def unselectTasks = tasks.now.foreach {
    t ⇒ t.selected.set(false)
  }

  def unselectEdges = edges.now.foreach {
    e ⇒ e.selected.set(false)
  }

  def removeTask(t: Task) = {
    tasks.update(ts => ts diff Seq(t))
    edges.update {
      es => es.filterNot(e ⇒ e.source.now == t || e.target.now == t)
    }
  }

  def removeEdge(e: Edge) = edges.update(es => es diff Seq(e))

  def addTask(task: Task): Unit = tasks.update(ts => ts :+ task)

  def addEdge(edge: Edge): Unit = edges.update(es => es :+ edge)
}