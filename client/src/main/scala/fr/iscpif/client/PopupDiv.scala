package fr.iscpif.client

import fr.iscpif.client.Test.ModifierSeq
import fr.iscpif.scaladget.stylesheet.all
import client.JsRxTags._
import org.scalajs.dom._
import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._
import all._
import rx._

/*
 * Copyright (C) 02/05/16 // mathieu.leclaire@openmole.org
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

object PopupDiv {

  sealed trait PopupPosition

  //object Left extends PopupPosition

  object Right extends PopupPosition

  //object Top extends PopupPosition

  object Bottom extends PopupPosition

  private val popups: Var[Seq[PopupDiv]] = Var(Seq())

  implicit class PopableHtmlElement(element: org.scalajs.dom.raw.HTMLElement) {

    def popup(innerDiv: TypedTag[org.scalajs.dom.raw.HTMLElement],
              position: PopupPosition = Bottom,
              popupStyle: ModifierSeq = whitePopup,
              arrowStyle: ModifierSeq = noArrow,
              onclose: () => Unit = () => {}) = {
      new PopupDiv(element, innerDiv, position, popupStyle, arrowStyle, onclose).popup
    }
  }

  implicit class PopableTypedTag(element: TypedTag[org.scalajs.dom.raw.HTMLElement]) {
    def popup(innerDiv: TypedTag[org.scalajs.dom.raw.HTMLElement],
              position: PopupPosition = Bottom,
              popupStyle: ModifierSeq = whitePopup,
              arrowStyle: ModifierSeq = noArrow,
              onclose: () => Unit = () => {}) =
      new PopableHtmlElement(element.render).popup(innerDiv, position, popupStyle, arrowStyle, onclose)
  }

  lazy val noArrow: ModifierSeq = Seq()
  lazy val whiteBottomArrow = arrow("white", Bottom)
  lazy val whiteRightArrow = arrow("white", Right)
  lazy val greyBottomArrow = arrow("#333", Bottom)
  lazy val greyRightArrow = arrow("#333", Right)

  lazy val whitePopup: ModifierSeq = Seq(
    all.absolutePosition,
    display := "inline-block",
    width := "auto",
    maxWidth := 200,
    height := "auto",
    padding := 16,
    borderRadius := "4px",
    backgroundColor := "white",
    zIndex := 1002,
    boxShadow := "0 8px 6px -6px black"
  )

  lazy val whitePopupWithBorder: ModifierSeq =
    whitePopup +++ (border := "0.1em solid #ccc")


  def arrow(color: String, position: PopupPosition): ModifierSeq = {
    val transparent = "5px solid transparent"
    val solid = s"5px solid $color"
    Seq(
      width := 0,
      height := 0) ++ {
      position match {
        case Bottom => Seq(
          borderLeft := transparent,
          borderRight := transparent,
          borderBottom := solid)
        case Right => Seq(
          borderTop := transparent,
          borderBottom := transparent,
          borderRight := solid)
      }
    }
  }

}

import PopupDiv._

class PopupDiv(val triggerElement: org.scalajs.dom.raw.HTMLElement,
               innerDiv: TypedTag[org.scalajs.dom.raw.HTMLElement],
               direction: PopupPosition,
               popupStyle: ModifierSeq,
               arrowStyle: ModifierSeq,
               onclose: () => Unit = () => {}) {

  val popupVisible = Var(false)
  popups() = popups() :+ this

  Obs(popupVisible) {
    if (!popupVisible()) onclose()
  }

  lazy val popupPosition: ModifierSeq = direction match {
    case Bottom => Seq(
      left := triggerElement.offsetLeft,
      top := triggerElement.offsetTop + triggerElement.offsetHeight + 5
    )

    case Right => Seq(
      left := triggerElement.offsetLeft + triggerElement.offsetWidth + 5,
      top := -triggerElement.offsetHeight / 2
    )
  }

  lazy val arrowPosition: ModifierSeq = direction match {
    case Bottom => all.marginLeft((triggerElement.offsetWidth / 2 - 3 + triggerElement.offsetLeft).toInt)
    case Right => Seq(
      all.marginLeft((triggerElement.offsetLeft + triggerElement.offsetWidth).toInt),
      all.marginTop(-(triggerElement.offsetTop + triggerElement.offsetHeight / 2 + 1).toInt)
    )
  }

  triggerElement.style.setProperty("cursor", "pointer")

  lazy val mainDiv = div(arrowStyle +++ arrowPosition)(
    innerDiv(popupStyle +++ popupPosition)
  ).render

  val popup = div(relativePosition)(
    triggerElement,
    Rx {
      if (popupVisible()) mainDiv
      else span(display := "none").render
    })

  def close = {
    // println("Close")
    popupVisible() = false
  }


  org.scalajs.dom.window.onmouseup = (m: org.scalajs.dom.raw.MouseEvent) => {

    popups().foreach { p =>
      println(" Iterate ")
      if (m.srcElement.isEqualNode(p.triggerElement)) p.popupVisible() = !p.popupVisible()
      else if (!isEqual(m.srcElement, p.mainDiv)) {
        p.popupVisible() = false
      }
    }
  }

  def isEqual(e1: Node, to: Node): Boolean = {
    if (e1.isEqualNode(to)) true
    else if (e1.isEqualNode(org.scalajs.dom.document.body)) false
    else isEqual(e1.parentNode, to)
  }

}
