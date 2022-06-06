package fr.iscpif.app


import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._

import scalatags.Text.all._
import scalatags.Text.{all => tags}

object RootPage {

  val route = {
    path("") {
      get {
        val ht =
          tags.html(
            tags.head(
              tags.meta(tags.httpEquiv := "Content-Type", tags.content := "text/html; charset=UTF-8"),
              tags.link(tags.rel := "stylesheet", tags.`type` := "text/css", href := "css/styleWUI.css"),
              tags.link(tags.rel := "stylesheet", tags.`type` := "text/css", href := "css/deps.css"),
              tags.script(tags.`type` := "text/javascript", tags.src := "js/demo.js")
            ),
            body(
              tags.div(id := "scalaWUI-content"),
              tags.script("scalaWui.graph();")
            )
          )

        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, ht.render))
      }
    } ~
    pathPrefix("js") {
      getFromDirectory("server/target/webapp/js")
    } ~
      pathPrefix("css") {
        getFromDirectory("server/target/webapp/css")
      }
  }

}
