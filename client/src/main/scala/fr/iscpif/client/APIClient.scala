package fr.iscpif.client

import endpoints4s.xhr
import endpoints4s.xhr.EndpointsSettings

/** Defines an HTTP client for the endpoints described in the `CounterEndpoints` trait.
 * The derived HTTP client uses XMLHttpRequest to perform requests and returns
 * results in a `js.Thenable`.
 */
object APIClient
  extends shared.APIEndpoint
    with xhr.future.Endpoints
    with xhr.JsonEntitiesFromCodecs {
  lazy val settings: EndpointsSettings = EndpointsSettings()
}