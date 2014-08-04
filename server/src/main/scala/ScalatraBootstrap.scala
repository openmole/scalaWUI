import fr.iscpif.app._
import org.scalatra._
import javax.servlet.ServletContext
import scala.concurrent.ExecutionContext.Implicits.global
//
class ScalatraBootstrap extends LifeCycle {

  override def init(context: ServletContext) {
  //val serv =  new MyScalatraServlet
   // pluginROutes.foreach{serv.addRoute}

    context.mount(new MyScalatraServlet, "/*")
  }
}
