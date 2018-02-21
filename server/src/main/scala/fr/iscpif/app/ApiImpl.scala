package fr.iscpif.app

object ApiImpl extends shared.Api {

  def uuid(): String = {
    println("UUUUUID")
    java.util.UUID.randomUUID.toString
  }
}