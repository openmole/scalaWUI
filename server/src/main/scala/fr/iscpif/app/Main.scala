package fr.iscpif.app

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object Main extends App {
  implicit val system: ActorSystem = ActorSystem("server-system")
  // needed for the future flatMap/onComplete in the end

  val routes = RootPage.route ~ APIServer.routes ~ DocumentationServer.routes

  val bindingFuture = Http().newServerAt("0.0.0.0", 8080).bindFlow(routes)
  bindingFuture.map(_.addToCoordinatedShutdown(hardTerminationDeadline = Duration.Zero))
  println("Press any key to stop")
  System.in.read()
  system.terminate()
}

// Additional route for serving the OpenAPI documentation
import endpoints4s.openapi.model.OpenApi
import endpoints4s.akkahttp.server

object DocumentationServer
  extends server.Endpoints
    with server.JsonEntitiesFromEncodersAndDecoders {

  val routes =
    endpoint(get(path / "documentation.json"), ok(jsonResponse[OpenApi]))
      .implementedBy(_ => APIDocumentation.api)

}