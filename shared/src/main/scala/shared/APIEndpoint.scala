package shared

import endpoints4s.{algebra, circe}
import io.circe._
import io.circe.generic.auto._

/** Defines the HTTP endpoints description of a web service implementing a counter.
 * This web service has two endpoints: one for getting the current value of the counter,
 * and one for incrementing it.
 */
trait APIEndpoint
  extends algebra.Endpoints
    with algebra.circe.JsonEntitiesFromCodecs
    with circe.JsonSchemas {

  /** Get the counter current value.
   * Uses the HTTP verb “GET” and URL path “/current-value”.
   * The response entity is a JSON document representing the counter value.
   */
  val uuid: Endpoint[Data.Foo, String] =
    endpoint(post(path / "uuid", jsonRequest[Data.Foo]), ok(jsonResponse[String]))

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
//  implicit lazy val fooEnc: Encoder[Data.Foo] = implicitly //JsonSchema[Data.Foo]
//  implicit lazy val fooDec: Decoder[Data.Foo] = implicitly //JsonSchema[Data.Foo]
  //implicit lazy val fooSchema: Codec[Data.Foo] = deriveCodec[Data.Foo]


}
