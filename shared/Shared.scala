package shared



trait Api {
  def uuid(): String = java.util.UUID.randomUUID.toString
}