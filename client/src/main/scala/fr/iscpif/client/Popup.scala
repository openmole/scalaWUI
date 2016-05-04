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

object Popup {

  sealed trait PopupPosition

  //object Left extends PopupPosition

  object Right extends PopupPosition

  //object Top extends PopupPosition

  object Bottom extends PopupPosition

  sealed trait PopupType

  object HoverPopup extends PopupType

  object ClickPopup extends PopupType

  private val popups: Var[Seq[Popup]] = Var(Seq())

  implicit class PopableTypedTag(element: TypedTag[org.scalajs.dom.raw.HTMLElement]) {
    def popup(innerDiv: TypedTag[org.scalajs.dom.raw.HTMLElement],
              position: PopupPosition = Bottom,
              popupStyle: ModifierSeq = whitePopup,
              arrowStyle: ModifierSeq = noArrow,
              onclose: () => Unit = () => {}) =
      new Popup(element.render, innerDiv, ClickPopup, position, popupStyle, arrowStyle, onclose).popup


    def tooltip(innerDiv: TypedTag[org.scalajs.dom.raw.HTMLElement],
                position: PopupPosition = Bottom,
                popupStyle: ModifierSeq = whitePopup,
                arrowStyle: ModifierSeq = noArrow,
                onclose: () => Unit = () => {}
               ) = new Popup(element.render, innerDiv, HoverPopup, position, popupStyle, arrowStyle, onclose).popup
  }

  def isEqual(e1: Node, to: Node): Boolean = {
    if (e1.isEqualNode(to)) true
    else if (e1.isEqualNode(org.scalajs.dom.document.body)) false
    else isEqual(e1.parentNode, to)
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
    padding := 8,
    borderRadius := "4px",
    backgroundColor := "white",
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

import Popup._

class Popup(val triggerElement: org.scalajs.dom.raw.HTMLElement,
            innerDiv: TypedTag[org.scalajs.dom.raw.HTMLElement],
            popupType: PopupType,
            direction: PopupPosition,
            popupStyle: ModifierSeq,
            arrowStyle: ModifierSeq,
            onclose: () => Unit = () => {}) {

  val popupVisible = Var(false)
  popups() = popups() :+ this

  Obs(popupVisible) {
    if (!popupVisible()) onclose()
  }
  
  val zPosition = zIndex := 1002

  lazy val popupPosition: ModifierSeq = {
    direction match {
      case Bottom => Seq(
        left := triggerElement.offsetLeft,
        top := triggerElement.offsetTop + triggerElement.offsetHeight + 5
      )

      case Right => Seq(
        left := triggerElement.offsetLeft + triggerElement.offsetWidth + 5,
        top := triggerElement.offsetTop
      )
    }
  } +++ zPosition +++ absolutePosition

  lazy val arrowPosition: ModifierSeq = {
    direction match {
    case Bottom =>
      Seq(
        left := (triggerElement.offsetWidth / 2 - 1 + triggerElement.offsetLeft).toInt,
        top := triggerElement.offsetTop + triggerElement.offsetHeight
      )
    case Right => Seq(
      left := (triggerElement.offsetLeft + triggerElement.offsetWidth).toInt,
      top := (triggerElement.offsetHeight / 2).toInt
    )
  }} +++ zPosition +++ absolutePosition


  triggerElement.style.setProperty("cursor", "pointer")
  popupType match {
    case HoverPopup =>
      triggerElement.onmouseover = (m: MouseEvent) => popupVisible() = true
      triggerElement.onmouseleave = (m: MouseEvent) => popupVisible() = false
    case _ =>
  }

  lazy val mainDiv = div(
    div( arrowStyle +++ arrowPosition),
    innerDiv( popupStyle +++ popupPosition)
  ).render

  val popup = div(relativePosition)(
    triggerElement,
    Rx {
      if (popupVisible()) mainDiv
      else span(display := "none").render
    })

  def close = popupVisible() = false


  // Manage the popups behavior when clicking in other popups or outside popups
  org.scalajs.dom.window.onmouseup = (m: org.scalajs.dom.raw.MouseEvent) => {
    popupType match {
      case ClickPopup =>
        val triggers = popups().map {
          _.triggerElement
        }
        val maindivs = popups().map {
          _.mainDiv
        }
        popups().foreach { p =>
          if (!triggers.filterNot {
            _ == p.triggerElement
          }.exists { t => isEqual(m.srcElement, t) }) {
            if (isEqual(m.srcElement, p.triggerElement)) p.popupVisible() = !p.popupVisible()
            else if (!maindivs.exists { md => isEqual(m.srcElement, md) }) {
              p.popupVisible() = false
            }
          }
        }
      case _ =>
    }
  }

}
