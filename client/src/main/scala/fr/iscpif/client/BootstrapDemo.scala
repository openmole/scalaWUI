package fr.iscpif.client


import fr.iscpif.scaladget.api.BootstrapTags._
import scalatags.JsDom.tags
import scalatags.JsDom.all._
import fr.iscpif.scaladget.stylesheet._
import fr.iscpif.scaladget.stylesheet.{bootstrap => bs, bootstrap2}
import scalatags.JsDom.{styles => sty}
import bs._

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

object BootstrapDemo {


  def build = {
    val bottom = glyph_triangle_bottom +++ (sty.left := "3px")
    tags.div(
      sty.left := "40px",
      sty.width := "100%",
      exclusiveButtonGroup(bootstrap2.sortingBar, stylesheet.selectedButton)(
        ExclusiveButton.twoGlyphStates(
          bottom,
          glyph_triangle_top,
          () ⇒ println("state 1"),
          () ⇒ println("state 2"),
          preString = "Aa"
        ),
        ExclusiveButton.twoGlyphStates(
          bottom,
          glyph_triangle_top,
          () ⇒ println("state 1"),
          () ⇒ println("state 2"),
          preGlyph = glyph_time
        ),
        ExclusiveButton.twoGlyphStates(
          bottom,
          glyph_triangle_top,
          () ⇒ println("state 1"),
          () ⇒ println("state 2"),
          preString = "Ko"
        ),
        ExclusiveButton.string("#", () => println("Yo #")),
        ExclusiveButton.string("Name", () => println("Yo name"))
      ).div
    )
  }


}
