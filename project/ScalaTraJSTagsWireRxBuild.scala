
import sbt._
import Keys._
import org.scalatra.sbt._
import org.scalatra.sbt.PluginKeys._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._

object ScalaTraJSTagsWireRxBuild extends Build {
  val Organization = "fr.iscpif"
  val Name = "ScalaTraJSTagsWireRx"
  val Version = "0.1.0-SNAPSHOT"
  val ScalaVersion = "2.11.6"
  val ScalatraVersion = "2.3.0"
  val Resolvers = Seq(Resolver.sonatypeRepo("snapshots"),
    "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
  )

  lazy val shared = project.in(file("./shared")).settings(
    scalaVersion := ScalaVersion
  )

  lazy val client = Project(
    "client",
    file("./client"),
    settings = Seq(
      version := Version,
      scalaVersion := ScalaVersion,
      resolvers ++= Resolvers,
      libraryDependencies ++= Seq(
        "com.lihaoyi" %%% "autowire" % "0.2.5",
        "com.lihaoyi" %%% "upickle" % "0.2.7",
        "com.lihaoyi" %%% "scalatags" % "0.4.6",
        "com.lihaoyi" %%% "scalarx" % "0.2.8",
        "fr.iscpif" %%% "scaladget" % "0.5.0-SNAPSHOT",
        "org.scala-js" %%% "scalajs-dom" % "0.8.0"
      )
    )
  ).dependsOn(shared) enablePlugins (ScalaJSPlugin)

  lazy val server = Project(
    "server",
    file("./server"),
    settings = ScalatraPlugin.scalatraWithJRebel ++ Seq(
      organization := Organization,
      name := Name,
      version := Version,
      scalaVersion := ScalaVersion,
      resolvers ++= Resolvers,
      libraryDependencies ++= Seq(
        "com.lihaoyi" %% "autowire" % "0.2.5",
        "com.lihaoyi" %% "upickle" % "0.2.7",
        "com.lihaoyi" %% "scalatags" % "0.4.6",
        "org.scalatra" %% "scalatra" % ScalatraVersion,
        "org.scalatra" %% "scalatra-specs2" % ScalatraVersion % "test",
        "ch.qos.logback" % "logback-classic" % "1.0.12" % "runtime",
        "org.eclipse.jetty" % "jetty-webapp" % "8.1.17.v20150415" % "container",
        "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" % "container;provided;test" /*artifacts (Artifact("javax.servlet", "jar", "jar"))*/
      )
    )
  ).dependsOn(shared)
}
