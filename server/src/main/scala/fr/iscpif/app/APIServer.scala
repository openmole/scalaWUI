package fr.iscpif.app

import cats.effect._
import endpoints4s.http4s.server
import org.http4s._

/** Defines a Play router (and reverse router) for the endpoints described
 * in the `CounterEndpoints` trait.
 */
object APIServer
  extends server.Endpoints[IO]
    with shared.APIEndpoint
    with server.JsonEntitiesFromCodecs { //with server.ChunkedEntities {

  //def stringCodec[A](implicit codec: JsonCodec[A]) = codec.stringCodec
  //trait JsonSchema

  /** Simple implementation of an in-memory counter */
 // val counter = Ref(0)

  // Implements the `currentValue` endpoint
  val uuidRoute =
    uuid.implementedBy(_ => java.util.UUID.randomUUID().toString)

  // Implements the `increment` endpoint
  val fooRoute =
    foo.implementedBy(_ => shared.Data.Foo(7))
//
//  val routes: Route =
//    uuidRoute ~ fooRoute

  val routes: HttpRoutes[IO] = HttpRoutes.of(
    routesFromEndpoints(uuidRoute, fooRoute)
  )

}