import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._

val Organization = "fr.iscpif"
val Name = "ScalaWUI"
val Version = "0.1.0-SNAPSHOT"
val ScalaVersion = "2.12.8"
val scalatraVersion = "2.6.5"
val jettyVersion = "9.4.19.v20190610"
val json4sVersion = "3.6.3"
val scalatagsVersion = "0.7.0"
val autowireVersion = "0.2.6"
val boopickleVersion = "1.3.1"
val rxVersion = "0.4.0"
val scaladgetVersion = "1.2.7"
val scalajsDomVersion = "0.9.7"
val Resolvers = Seq(Resolver.sonatypeRepo("snapshots"),
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
)

lazy val shared = project.in(file("shared")) settings(
  scalaVersion := ScalaVersion
) enablePlugins (ScalaJSPlugin)


lazy val go = taskKey[Unit]("go")

lazy val client = project.in(file("client")) enablePlugins (ExecNpmPlugin) settings(
  version := Version,
  scalaVersion := ScalaVersion,
  resolvers in ThisBuild ++= Resolvers,
  skip in packageJSDependencies := false,
  libraryDependencies ++= Seq(
    "com.lihaoyi" %%% "autowire" % autowireVersion,
    "io.suzaku" %%% "boopickle" % boopickleVersion,
    "com.lihaoyi" %%% "scalatags" % scalatagsVersion,
    "com.lihaoyi" %%% "scalarx" % rxVersion,
    "fr.iscpif.scaladget" %%% "tools" % scaladgetVersion,
    "fr.iscpif.scaladget" %%% "svg" % scaladgetVersion,
    "fr.iscpif.scaladget" %%% "bootstrapnative" % scaladgetVersion,
    "org.scala-js" %%% "scalajs-dom" % scalajsDomVersion,
    "org.json4s" %% "json4s-jackson" % json4sVersion
  )
) dependsOn (shared)

lazy val server = project.in(file("server")) settings(
  organization := Organization,
  name := Name,
  version := Version,
  scalaVersion := ScalaVersion,
  resolvers ++= Resolvers,
  libraryDependencies ++= Seq(
    "com.lihaoyi" %% "autowire" % autowireVersion,
    "io.suzaku" %% "boopickle" % boopickleVersion,
    "com.lihaoyi" %% "scalatags" % scalatagsVersion,
    "org.scalatra" %% "scalatra" % scalatraVersion,
    "ch.qos.logback" % "logback-classic" % "1.1.3" % "runtime",
    "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided",
    "org.eclipse.jetty" % "jetty-webapp" % jettyVersion,
    "org.eclipse.jetty" % "jetty-server" % jettyVersion
  )
) dependsOn (shared) enablePlugins (ScalatraPlugin)


lazy val bootstrap = project.in(file("target/bootstrap")) settings(
  version := Version,
  scalaVersion := ScalaVersion,
  go := {

      val jsBuild = (fullOptJS in client in Compile).value.data
      val demoTarget = (target in server in Compile).value

      val demoResource = (resourceDirectory in client in Compile).value
      val dependencyJS = (dependencyFile in client in Compile).value
      val depsCSS = (cssFile in client in Compile).value

      IO.copyFile(jsBuild, demoTarget / "webapp/js/demo.js")
      IO.copyFile(dependencyJS, demoTarget / "webapp/js/deps.js")
      IO.copyDirectory(depsCSS, demoTarget / "webapp/css")
      IO.copyDirectory(demoResource, demoTarget)
  }) dependsOn(client, server)