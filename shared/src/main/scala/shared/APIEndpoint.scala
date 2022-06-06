package shared

import endpoints4s.{algebra, generic}

/** Defines the HTTP endpoints description of a web service implementing a counter.
 * This web service has two endpoints: one for getting the current value of the counter,
 * and one for incrementing it.
 */
trait APIEndpoint
  extends algebra.Endpoints
    with algebra.JsonEntitiesFromSchemas
    with generic.JsonSchemas {

  /** Get the counter current value.
   * Uses the HTTP verb “GET” and URL path “/current-value”.
   * The response entity is a JSON document representing the counter value.
   */
  val uuid: Endpoint[Unit, String] =
    endpoint(get(path / "uuid"), ok(jsonResponse[String]))

  /** Increments the counter value.
   * Uses the HTTP verb “POST” and URL path “/increment”.
   * The request entity is a JSON document representing the increment to apply to the counter.
   * The response entity is empty.
   */
  val foo: Endpoint[Unit, Data.Foo] =
    endpoint(
      get(path / "foo"),
      ok(jsonResponse[Data.Foo])
    )

  // Generically derive the JSON schema of our `Counter`
  // and `Increment` case classes defined thereafter
  implicit lazy val fooSchema: JsonSchema[Data.Foo] = genericJsonSchema

}
