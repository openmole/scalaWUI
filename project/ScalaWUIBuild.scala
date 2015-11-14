
import sbt._
import Keys._
import org.scalatra.sbt._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import com.earldouglas.xsbtwebplugin.PluginKeys.webappResources
import java.io.File

object ScalaWUIBuild extends Build {
  val Organization = "fr.iscpif"
  val Name = "ScalaTraJSTagsWireRx"
  val Version = "0.1.0-SNAPSHOT"
  val ScalaVersion = "2.11.7"
  val ScalatraVersion = "2.3.0"
  val Resolvers = Seq(Resolver.sonatypeRepo("snapshots"),
    "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
  )

  lazy val shared = project.in(file("./shared")).settings(
    scalaVersion := ScalaVersion
  )

  lazy val client = Project(
    "client",
    file("client"),
    settings = Seq(
      version := Version,
      scalaVersion := ScalaVersion,
      resolvers in ThisBuild ++= Resolvers,
      libraryDependencies ++= Seq(
        "com.lihaoyi" %%% "autowire" % "0.2.5",
        "com.lihaoyi" %%% "upickle" % "0.3.6",
        "com.lihaoyi" %%% "scalatags" % "0.5.2",
        "com.lihaoyi" %%% "scalarx" % "0.2.8",
        "fr.iscpif" %%% "scaladget" % "0.7.0",
        "org.singlespaced" %%% "scalajs-d3" % "0.1.1",
        "org.scala-js" %%% "scalajs-dom" % "0.8.0"
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
      webappResources in Compile := Seq(target.value / "webapp"),
      libraryDependencies ++= Seq(
        "com.lihaoyi" %% "autowire" % "0.2.5",
        "com.lihaoyi" %% "upickle" % "0.3.6",
        "com.lihaoyi" %% "scalatags" % "0.5.2",
        "org.scalatra" %% "scalatra" % ScalatraVersion,
        "org.scalatra" %% "scalatra-specs2" % ScalatraVersion % "test",
        "ch.qos.logback" % "logback-classic" % "1.0.12" % "runtime",
        "org.eclipse.jetty" % "jetty-webapp" % "8.1.17.v20150415" % "container",
        "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" % "container;provided;test"
      )
    )
  ).dependsOn(shared)

  lazy val go = taskKey[Unit]("go")

  lazy val bootstrap = Project(
    "bootstrap",
    file("target/bootstrap"),
    settings = Seq(
      version := Version,
      scalaVersion := ScalaVersion,
      (go <<= (fullOptJS in client in Compile, resourceDirectory in client in Compile, target in server in Compile) map { (ct, r, st) =>
        copy(ct, r, new File(st,"webapp"))
      }
        )
    )
  ) dependsOn(client, server)


  private def copy(clientTarget: Attributed[File], resources: File, webappServerTarget: File) = {
    clientTarget.map { ct =>
      recursiveCopy(new File(resources, "webapp"), webappServerTarget)
      recursiveCopy(ct, new File(webappServerTarget, "js/" + ct.getName))
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
