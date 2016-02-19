package shared
import fr.iscpif.ext.Data._


trait Api {
  def uuid(): String = java.util.UUID.randomUUID.toString
  def runningData(i: Int): RunningData

}