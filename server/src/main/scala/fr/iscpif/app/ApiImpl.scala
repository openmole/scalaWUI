package org.openmole.app

object ApiImpl extends shared.Api {

  def uuid(): String = {
    java.util.UUID.randomUUID.toString
  }

  def foo(): shared.Data.Foo = new shared.Data.Foo(7)
}
