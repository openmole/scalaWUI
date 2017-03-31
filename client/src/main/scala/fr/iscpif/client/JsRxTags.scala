
/*
 * Copyright (C) 21/07/14 mathieu
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
package client


import org.scalajs.dom.raw.{HTMLDivElement, HTMLElement, Node, SVGElement}

import scalatags.JsDom._
import scala.util.{Failure, Success}
import all._
import rx._
import org.scalajs.dom.Element

import scaladget.stylesheet.all._

/**
  * A minimal binding between Scala.Rx and Scalatags and Scala-Js-Dom
  */
object JsRxTags {

  implicit val ctx: Ctx.Owner = Ctx.Owner.safe()

  /**
    * Wraps reactive strings in spans, so they can be referenced/replaced
    * when the Rx changes.
    */
  implicit def RxStr[T](r: Rx[T])(implicit f: T ⇒ Modifier): Modifier = {
    rxHTMLMod(Rx(span(r())))
  }


//  implicit def RxModifierSeq(r: TypedTag[Rx[ModifierSeq]]): TypedTag[ModifierSeq] = {
//    r.copy(modifiers = r.modifiers)
//  }

  /**
    * Sticks some Rx into a Scalatags fragment, which means hooking up an Obs
    * to propagate changes into the DOM via the element's ID. Monkey-patches
    * the Obs onto the element itself so we have a reference to kill it when
    * the element leaves the DOM (e.g. it gets deleted).
    */
  implicit def rxHTMLMod[T <: HtmlTag](r: Rx[T]): Modifier = bindNode(rxHTMLNode(r))

  // implicit def rxHTMLTagedType[T <: HtmlTag](r: Rx[T]): Modifier = bindNode(rxHTMLNode(r))

  implicit def rxHTMLNode[T <: TypedTag[HTMLElement]](r: Rx[T]): HTMLElement = {
    def rSafe = r.toTry match {
      case Success(v) ⇒ v.render
      case Failure(e) ⇒ span(e.toString, backgroundColor := "red").render
    }

    var last = rSafe
    r.triggerLater {
      val newLast = rSafe
      last.parentNode.replaceChild(newLast, last)
      last = newLast
    }
    last
  }

  /**
    * Idem for SVG elements
    */
  implicit def rxSVGMod[T <: TypedTag[SVGElement]](r: Rx[T]): SVGElement = {
   def rSafe = r.now.render//r.toTry match {
//      case Success(v) ⇒ v.render
//      case Failure(e) ⇒ span(e.toString, backgroundColor := "red").render
//    }

    var last = rSafe
    r.triggerLater {
      val newLast = rSafe
      last.parentNode.replaceChild(newLast, last)
      last = newLast
    }
    last
  }

  implicit def RxAttrValue[T: scalatags.JsDom.AttrValue] = new scalatags.JsDom.AttrValue[Rx.Dynamic[T]] {
    def apply(t: Element, a: Attr, r: Rx.Dynamic[T]): Unit = {
      r.trigger {
        implicitly[scalatags.JsDom.AttrValue[T]].apply(t, a, r.now)
      }
    }
  }

  implicit def RxStyleValue[T: scalatags.JsDom.StyleValue] = new scalatags.JsDom.StyleValue[Rx.Dynamic[T]] {
    def apply(t: Element, s: Style, r: Rx.Dynamic[T]): Unit = {
      r.trigger {
        implicitly[scalatags.JsDom.StyleValue[T]].apply(t, s, r.now)
      }
    }
  }

  // Convenient implicit conversions
  def rxIf[T](dynamic: Rx.Dynamic[Boolean], yes: T, no: T) = {
    rx.Rx {
      if (dynamic()) yes
      else no
    }
  }


  def rxIf[T](dynamic: Var[Boolean], yes: T, no: T) = {
    rx.Rx {
      if (dynamic()) yes
      else no
    }
  }

  implicit def rxNode(r: Rx[Node]): Node = {

    def oo = {
      def rSafe = r.now

      var last = rSafe

      r.triggerLater {
        val newLast = rSafe
        last.parentNode.replaceChild(newLast, last)
        last = newLast
      }
      last
    }

    bindNode(oo).render
  }


  //  implicit val ctx: Ctx.Owner = Ctx.Owner.safe()
  //
  //  /**
  //    * Wraps reactive strings in spans, so they can be referenced/replaced
  //    * when the Rx changes.
  //    */
  //  implicit def RxStr[T](r: Rx[T])(implicit f: T ⇒ Modifier): Modifier = {
  //    rxHTMLMod(Rx(span(r())))
  //  }
  //
  //  implicit def rxNode(r: Rx[Node]): Node = {
  //
  //    def oo = {
  //      def rSafe = r.now
  //
  //      var last = rSafe
  //
  //      r.triggerLater {
  //        val newLast = rSafe
  //        last.parentNode.replaceChild(newLast, last)
  //        last = newLast
  //      }
  //      last
  //    }
  //
  //    bindNode(oo).render
  //  }
  //  /**
  //    * Sticks some Rx into a Scalatags fragment, which means hooking up an Obs
  //    * to propagate changes into the DOM via the element's ID. Monkey-patches
  //    * the Obs onto the element itself so we have a reference to kill it when
  //    * the element leaves the DOM (e.g. it gets deleted).
  //    */
  //  implicit def rxHTMLMod[T <: HtmlTag](r: Rx[T]): Modifier = bindNode(rxHTMLNode(r))
  //
  //  // implicit def rxHTMLTagedType[T <: HtmlTag](r: Rx[T]): Modifier = bindNode(rxHTMLNode(r))
  //
  //  implicit def rxHTMLNode[T <: HtmlTag](r: Rx[T]): Node = {
  //    def rSafe = r.toTry match {
  //      case Success(v) ⇒ v.render
  //      case Failure(e) ⇒ span(e.toString, backgroundColor := "red").render
  //    }
  //    var last = rSafe
  //    r.triggerLater {
  //      val newLast = rSafe
  //      last.parentNode.replaceChild(newLast, last)
  //      last = newLast
  //    }
  //    last
  //  }
  //
  //  /**
  //    * Idem for SVG elements
  //    */
  //  implicit def rxSVGMod[T <: TypedTag[SVGElement]](r: Rx[T]): Modifier = {
  //    def rSafe = r.toTry match {
  //      case Success(v) ⇒ v.render
  //      case Failure(e) ⇒ span(e.toString, backgroundColor := "red").render
  //    }
  //    var last = rSafe
  //    r.triggerLater {
  //      val newLast = rSafe
  //      last.parentNode.replaceChild(newLast, last)
  //      last = newLast
  //    }
  //    last
  //  }
  //
  //  implicit def RxAttrValue[T: scalatags.JsDom.AttrValue] = new scalatags.JsDom.AttrValue[Rx.Dynamic[T]] {
  //    def apply(t: Element, a: Attr, r: Rx.Dynamic[T]): Unit = {
  //      r.trigger {
  //        implicitly[scalatags.JsDom.AttrValue[T]].apply(t, a, r.now)
  //      }
  //    }
  //  }
  //
  //  implicit def RxStyleValue[T: scalatags.JsDom.StyleValue] = new scalatags.JsDom.StyleValue[Rx.Dynamic[T]] {
  //    def apply(t: Element, s: Style, r: Rx.Dynamic[T]): Unit = {
  //      r.trigger {
  //        implicitly[scalatags.JsDom.StyleValue[T]].apply(t, s, r.now)
  //      }
  //    }
  //  }

}