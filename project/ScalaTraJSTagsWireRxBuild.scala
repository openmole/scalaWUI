import sbt._
import Keys._
import org.scalatra.sbt._
import org.scalatra.sbt.PluginKeys._
import fr.iscpif.jsmanager.JSManagerPlugin._

object ScalaTraJSTagsWireRxBuild extends Build {
  val Organization = "fr.iscpif"
  val Name = "ScalaTraJSTagsWireRx"
  val Version = "0.1.0-SNAPSHOT"
  val ScalaVersion = "2.11.2"
  val ScalatraVersion = "2.3.0"
  val Resolvers = Seq(Resolver.sonatypeRepo("snapshots"),
    "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
    Resolver.url("scala-js-releases",
      url("http://dl.bintray.com/content/scala-js/scala-js-releases"))(
        Resolver.ivyStylePatterns))

  lazy val shared = project.in(file("./shared")).settings(
    scalaVersion := ScalaVersion
  ).settings(jsManagerSettings: _*)

  lazy val client = Project(
    "client",
    file("./client"),
    settings = Defaults.defaultSettings ++ jsManagerSettings ++ Seq(
      version := Version,
      scalaVersion := ScalaVersion,
      resolvers ++= Resolvers,
      libraryDependencies ++= Seq(
        "com.lihaoyi" %%% "autowire" % "0.2.2",
        "com.lihaoyi" %%% "upickle" % "0.2.2",
        "com.scalatags" %%% "scalatags" % "0.4.0",
        "com.scalarx" %%% "scalarx" % "0.2.6",
        "org.scala-lang.modules.scalajs" %%% "scalajs-dom" % "0.6",
        "org.scala-lang.modules.scalajs" %%% "scalajs-jquery" % "0.6"
      ),
      //jsCall := "Client().run();",
      outputPath := "server/src/main/webapp/"
    )
  ).dependsOn(shared)

  lazy val server = Project(
    "server",
    file("./server"),
    settings = Defaults.defaultSettings ++ ScalatraPlugin.scalatraWithJRebel ++ Seq(
      organization := Organization,
      name := Name,
      version := Version,
      scalaVersion := ScalaVersion,
      resolvers ++= Resolvers,
      libraryDependencies ++= Seq(
        "com.lihaoyi" %% "autowire" % "0.2.1",
        "com.lihaoyi" %% "upickle" % "0.2.1",
        "com.scalatags" %%% "scalatags" % "0.4.0",
        "org.scalatra" %% "scalatra" % ScalatraVersion,
        "org.scalatra" %% "scalatra-specs2" % ScalatraVersion % "test",
        "ch.qos.logback" % "logback-classic" % "1.0.12" % "runtime",
        "org.eclipse.jetty" % "jetty-webapp" % "8.1.8.v20121106" % "container",
        "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" % "container;provided;test" artifacts (Artifact("javax.servlet", "jar", "jar"))
      )
    )
  ).dependsOn(shared)
}
