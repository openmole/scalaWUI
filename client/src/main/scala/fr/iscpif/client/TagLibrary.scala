package fr.iscpif.client


import org.scalajs.dom.raw._
import scalatags.JsDom.all._
import scalatags.JsDom.TypedTag
import fr.iscpif.scaladget.api._
import fr.iscpif.scaladget.api.{BootstrapTags ⇒ bs}
import client.JsRxTags._
import bs._
import rx._

/*
 * Copyright (C) 24/03/16 // mathieu.leclaire@openmole.org
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

object TagLibrary {

  // TO PORT IN SCALADGET
  private val glyphPrefix = "glyphicon "
  val glyph_upload_alt = glyphPrefix + "glyphicon-upload"
  val glyph_arrow_right = glyphPrefix + "glyphicon-arrow-right"
  val glyph_arrow_left = glyphPrefix + "glyphicon-arrow-left"
  val glyph_arrow_right_and_left = glyphPrefix + "glyphicon-resize-horizontal"
  val glyph_filter = glyphPrefix + "glyphicon-filter"
  val glyph_copy = glyphPrefix + "glyphicon-copy"
  val glyph_paste = glyphPrefix + "glyphicon-paste"
  val glyph_time = glyphPrefix + "glyphicon-time"
  val glyph_alph_sorting = glyphPrefix + "glyphicon-sort-by-alphabet"
  val glyph_triangle_bottom = glyphPrefix + "glyphicon-triangle-bottom"
  val glyph_triangle_top = glyphPrefix + "glyphicon-triangle-top"


  def glyphButton(text: String, buttonCB: ClassKeyAggregator, glyCA: ClassKeyAggregator, todo: () ⇒ Unit): TypedTag[HTMLSpanElement] =
    bs.span("btn " + buttonCB.key)(text)(cursor := "pointer", `type` := "button")(onclick := {
      () ⇒ todo()
    })(bs.span(glyCA))

  def buttonGroupExclusive(keys: ClassKeyAggregator = emptyCK, selectionCKA: ClassKeyAggregator)(buttons: ExclusiveButton*) = new ExclusiveGroup(keys, selectionCKA, buttons)

  def twoStatesGlyphButton(
                            glyph1: ClassKeyAggregator,
                            glyph2: ClassKeyAggregator,
                            todo1: () ⇒ Unit,
                            todo2: () ⇒ Unit,
                            preString: String = "",
                            preGlyph: ClassKeyAggregator = emptyCK
                          ) = new TwoStatesGlyphButton(glyph1, glyph2, todo1, todo2, preString, preGlyph)


  sealed trait ExclusiveButton {
    def action: () ⇒ Unit
  }

  trait ExclusiveGlyphButton extends ExclusiveButton {
    def glyph: ClassKeyAggregator
  }

  trait ExclusiveStringButton extends ExclusiveButton {
    def title: String
  }

  case class TwoStatesGlyphButton(glyph: ClassKeyAggregator,
                                  glyph2: ClassKeyAggregator,
                                  action: () ⇒ Unit,
                                  action2: () ⇒ Unit,
                                  preString: String,
                                  preGlyph: ClassKeyAggregator
                                 ) extends ExclusiveButton {
    val selected = Var(glyph)

    val div: Modifier = Rx {
      glyphButton(preString, key("left5") + btn_default + preGlyph, selected(), () ⇒ {
        if (selected() == glyph) {
          selected() = glyph2
          action2()
        }
        else {
          selected() = glyph
          action()
        }
      })
    }
  }

  object ExclusiveButton {
    def string(t: String, a: () ⇒ Unit) = new ExclusiveStringButton {
      def title = t

      def action = a
    }

    def glyph(g: ClassKeyAggregator, a: () ⇒ Unit) = new ExclusiveGlyphButton {
      def glyph = g

      def action = a
    }

    def twoGlyphStates(
                        glyph1: ClassKeyAggregator,
                        glyph2: ClassKeyAggregator,
                        todo1: () ⇒ Unit,
                        todo2: () ⇒ Unit,
                        preString: String = "",
                        preGlyph: ClassKeyAggregator = emptyCK
                      ) = twoStatesGlyphButton(glyph1, glyph2, todo1, todo2, preString, preGlyph)
  }


  class ExclusiveGroup(keys: ClassKeyAggregator, selectionCKA: ClassKeyAggregator, buttons: Seq[ExclusiveButton]) {
    val selected = Var(buttons.head)

    def buttonBackground(b: ExclusiveButton) = if (b == selected()) selectionCKA else btn_default

    val div: HTMLElement = Rx {
      bs.div(keys + "btn-group")(
        for (b ← buttons) yield {
          b match {
            case s: ExclusiveStringButton ⇒ bs.button(s.title, buttonBackground(s) + "stringInGroup", action(b, s.action))
            case g: ExclusiveGlyphButton ⇒ bs.glyphButton("", buttonBackground(g), g.glyph, action(b, g.action))
            case ts: TwoStatesGlyphButton ⇒
              println("UUU -> " + ts.selected() + " // " + ts.glyph + " // " + ts.glyph2)
              twoStatesGlyphButton(ts.glyph, ts.glyph2, action(ts, ts.action), action(ts, ts.action2), ts.preString, buttonBackground(ts) + ts.preGlyph).div
            case _ ⇒ bs.button("??")
          }
        }
      )
    }

    private def action(b: ExclusiveButton, a: () ⇒ Unit) = () ⇒ {
      if (selected() != b) selected() = b
      a()
    }


    def reset = selected() = buttons.head
  }

}
