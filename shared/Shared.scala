package shared

import scala.annotation.ClassfileAnnotation

class Web extends ClassfileAnnotation

@Web
trait Api {
  def hello(a: Int): Int
}