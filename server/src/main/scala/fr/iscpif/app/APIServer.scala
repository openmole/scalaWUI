package fr.iscpif.app

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import endpoints4s.akkahttp.server

/** Defines a Play router (and reverse router) for the endpoints described
 * in the `CounterEndpoints` trait.
 */
object APIServer
  extends shared.APIEndpoint
    with server.Endpoints
    with server.JsonEntitiesFromSchemas {

  /** Simple implementation of an in-memory counter */
 // val counter = Ref(0)

  // Implements the `currentValue` endpoint
  val uuidRoute =
    uuid.implementedBy(_ => java.util.UUID.randomUUID().toString)

  // Implements the `increment` endpoint
  val fooRoute =
    foo.implementedBy(_ => shared.Data.Foo(7))

  val routes: Route =
    uuidRoute ~ fooRoute

}