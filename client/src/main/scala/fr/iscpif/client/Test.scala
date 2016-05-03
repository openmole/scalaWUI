package fr.iscpif.client

import fr.iscpif.scaladget.stylesheet.all

import org.scalajs.dom
import scalatags.JsDom.all._
import scalatags.generic.StylePair

/*
 * Copyright (C) 08/04/16 // mathieu.leclaire@openmole.org
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

object Test {

  type ClassAttrPair = scalatags.generic.AttrPair[dom.Element, String]
  type ModifierSeq = Seq[Modifier]
  val emptyMod: ModifierSeq = Seq()


  def pairing(a: String, b: String): ClassAttrPair = `class` := (a.split(" ") ++ b.split(" ")).distinct.mkString(" ")

  implicit def modifierToModifierSeq(p: Modifier): ModifierSeq = Seq(p)

  implicit def stringToModifierSeq(classString: String): ModifierSeq = toClass(classString)

  implicit class ComposableClassAttrPair[P <: ClassAttrPair](pair: P) {
    def +++(pair2: ClassAttrPair): ClassAttrPair = {
      if (pair.a.name != "class" || pair2.a.name != "class") toClass("ClassError")
      else pairing(pair.v, pair2.v)
    }

    def +++(mod: ModifierSeq): ModifierSeq = {
      println("here !!")
      //pair +: mod
      mod +++ pair
    }

    def +++(sty: StylePair[dom.Element, _]): ModifierSeq = Seq(pair, sty)
  }

  implicit class ComposableModifierSeq(modifierSeq: ModifierSeq) {
    private def findClassAttrPair(modifierSeq: ModifierSeq) =
      modifierSeq.collect { case a: ClassAttrPair => a }.filter { x =>
        x.a.name == "class"
      }


    def +++(classPair: ClassAttrPair): ModifierSeq = {
      val attrPair = findClassAttrPair(modifierSeq)

      if (attrPair.isEmpty) modifierSeq :+ classPair
      else modifierSeq.filterNot(_ == attrPair.head) :+ pairing(attrPair.head.v, classPair.v)
    }

    def +++(modifierSeq2: ModifierSeq): ModifierSeq = {
      val attrPair = findClassAttrPair(modifierSeq)
      val attrPair2 = findClassAttrPair(modifierSeq2)
      val classPairing =
        if (attrPair.isEmpty && attrPair2.isEmpty) Seq()
        else Seq((attrPair ++ attrPair2).reduce { (a, b) => pairing(a.v, b.v) })

      modifierSeq.filterNot {
        _ == attrPair
      } ++ modifierSeq2.filterNot {
        _ == attrPair2
      } ++ classPairing

    }

    def divCSS(cssClass: String) = div(`class` := cssClass)
  }

  // Convenient implicit conversions
  implicit def condOnModifierSeq3(t: Tuple3[Boolean, ModifierSeq, ModifierSeq]): ModifierSeq = if (t._1) t._2 else t._3

  implicit def condOnModifierSeq2(t: Tuple2[Boolean, ModifierSeq]): ModifierSeq = condOnModifierSeq3(t._1, t._2, emptyMod)


  def toClass(s: String): ClassAttrPair = `class` := s

  // Explicit builders for ModifierSeq (from string or from condition and two ModifierSeq alternatives)
  def ms(s: String): ModifierSeq = Seq(`class` := s)

  def ms(cond: Boolean, ms1: ModifierSeq, ms2: ModifierSeq = emptyMod): ModifierSeq = condOnModifierSeq3(cond, ms1, ms2)

  def build = {
    val oo = all.glyph_upload +++ "fileUpload glyphmenu"
    div(oo)
  }
}
