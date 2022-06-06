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