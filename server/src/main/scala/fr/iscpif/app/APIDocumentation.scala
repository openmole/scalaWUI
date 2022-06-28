package fr.iscpif.app

import endpoints4s.openapi
import endpoints4s.openapi.model.{Info, OpenApi}
import shared.APIEndpoint

/** Generates OpenAPI documentation for the endpoints described in the `CounterEndpoints` trait.
 */
object APIDocumentation
  extends APIEndpoint
    with openapi.Endpoints
    with openapi.JsonEntitiesFromSchemas {

  val api: OpenApi =
    openApi(
      Info(title = "Dummy API", version = "1.0.0")
    )(uuid, foo)

}

// Additional route for serving the OpenAPI documentation
import endpoints4s.openapi.model.OpenApi
import endpoints4s.http4s.server
import org.http4s._
import cats.effect._

object DocumentationServer
  extends server.Endpoints[IO]
    with server.JsonEntitiesFromEncodersAndDecoders {

  val routes =
    HttpRoutes.of(endpoint(get(path / "documentation.json"), ok(jsonResponse[OpenApi]))
      .implementedBy(_ => APIDocumentation.api))

}