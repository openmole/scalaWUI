
import sbt._
import Keys._
import org.scalatra.sbt._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import com.earldouglas.xwp._
import java.io.File

object ScalaWUIBuild extends Build {
  val Organization = "fr.iscpif"
  val Name = "ScalaWUI"
  val Version = "0.1.0-SNAPSHOT"
  val ScalaVersion = "2.11.7"
  val scalatraVersion = "2.4.0"
  val jettyVersion = "9.3.7.v20160115"
  val json4sVersion = "3.3.0"
  val scalatagsVersion = "0.5.4"
  val autowireVersion = "0.2.5"
  val upickleVersion = "0.4.1"
  val rxVersion = "0.3.1"
  val scaladgetVersion = "0.8.1"
  val scalajsDomVersion = "0.9.0"
  val jqueryVersion = "2.2.1"
  val Resolvers = Seq(Resolver.sonatypeRepo("snapshots"),
    "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
  )

  lazy val shared = project.in(file("./shared")).settings(
    scalaVersion := ScalaVersion
  )

val jqueryPath = s"META-INF/resources/webjars/jquery/$jqueryVersion/jquery.js"

  lazy val client = Project(
    "client",
    file("client"),
    settings = Seq(
      version := Version,
      scalaVersion := ScalaVersion,
      resolvers in ThisBuild ++= Resolvers,
      skip in packageJSDependencies := false,
      jsDependencies += "org.webjars" % "d3js" % "3.5.12" / "d3.min.js",
      //jsDependencies += "org.webjars" % "jquery" % "2.2.1" / "jquery.js",
      jsDependencies += "org.webjars" % "jquery" % jqueryVersion / jqueryPath minified jqueryPath.replace(".js", ".min.js"),
      jsDependencies += "org.webjars" % "bootstrap" % "3.3.6" / "js/bootstrap.js" dependsOn jqueryPath minified "js/bootstrap.min.js",
      //  jsDependencies += "org.webjars" % "bootstrap" % "3.3.6" / "js/bootstrap.min.js",
      libraryDependencies ++= Seq(
        "com.lihaoyi" %%% "autowire" % autowireVersion,
        "com.lihaoyi" %%% "upickle" % upickleVersion,
        "com.lihaoyi" %%% "scalatags" % scalatagsVersion,
        "com.lihaoyi" %%% "scalarx" % rxVersion,
        "fr.iscpif" %%% "scaladget" % scaladgetVersion,
        "org.scala-js" %%% "scalajs-dom" % scalajsDomVersion,
        "org.json4s" %% "json4s-jackson" % json4sVersion
      )
    )
  ).dependsOn(shared) enablePlugins (ScalaJSPlugin)

  lazy val server = Project(
    "server",
    file("server"),
    settings = ScalatraPlugin.scalatraWithJRebel ++ Seq(
      organization := Organization,
      name := Name,
      version := Version,
      scalaVersion := ScalaVersion,
      resolvers ++= Resolvers,
      libraryDependencies ++= Seq(
        "com.lihaoyi" %% "autowire" % autowireVersion,
        "com.lihaoyi" %% "upickle" % upickleVersion,
        "com.lihaoyi" %% "scalatags" % scalatagsVersion,
        "org.scalatra" %% "scalatra" % scalatraVersion,
        "ch.qos.logback" % "logback-classic" % "1.1.3" % "runtime",
        "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided",
        "org.eclipse.jetty" % "jetty-webapp" % jettyVersion % "container"
      )
    )
  ).dependsOn(shared) enablePlugins (JettyPlugin)

  lazy val go = taskKey[Unit]("go")

  lazy val bootstrap = Project(
    "bootstrap",
    file("target/bootstrap"),
    settings = Seq(
      version := Version,
      scalaVersion := ScalaVersion,
      (go <<= (fullOptJS in client in Compile, resourceDirectory in client in Compile, target in server in Compile) map { (ct, r, st) =>
        copy(ct, r, new File(st, "webapp"))
      }
        )
    )
  ) dependsOn(client, server)


  private def copy(clientTarget: Attributed[File], resources: File, webappServerTarget: File) = {
    clientTarget.map { ct =>
      val depName = ct.getName.replace("opt.js", "jsdeps.min.js")
      recursiveCopy(new File(resources, "webapp"), webappServerTarget)
      recursiveCopy(ct, new File(webappServerTarget, "js/" + ct.getName))
      recursiveCopy(new File(ct.getParent, depName), new File(webappServerTarget, "js/" + depName))
    }
  }

  private def recursiveCopy(from: File, to: File): Unit = {
    if (from.isDirectory) {
      to.mkdirs()
      for {
        f ← from.listFiles()
      } recursiveCopy(f, new File(to, f.getName))
    }
    else if (!to.exists() || from.lastModified() > to.lastModified) {
      println(s"Copy file $from to $to ")
      from.getParentFile.mkdirs
      IO.copyFile(from, to, preserveLastModified = true)
    }
  }

}
