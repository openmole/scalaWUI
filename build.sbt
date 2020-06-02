import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._

val Organization = "org.openmole"
val Name = "ScalaWUI"
val Version = "0.3"
val ScalaVersion = "2.13.2"
val scalatraVersion = "2.7.0"
val jettyVersion = "9.4.28.v20200408"
val json4sVersion = "3.6.7"
val scalatagsVersion = "0.9.1"
val autowireVersion = "0.3.2"
val boopickleVersion = "1.3.2"
val rxVersion = "0.4.2"
val scaladgetVersion = "1.3.3"
val scalajsDomVersion = "1.0.0"

lazy val shared = project.in(file("shared")) settings(
  scalaVersion := ScalaVersion
) enablePlugins (ScalaJSPlugin)


lazy val go = taskKey[Unit]("go")

lazy val client = project.in(file("client")) enablePlugins (ExecNpmPlugin) settings(
  version := Version,
  scalaVersion := ScalaVersion,
  skip in packageJSDependencies := false,
  libraryDependencies ++= Seq(
    "com.lihaoyi" %%% "autowire" % autowireVersion,
    "io.suzaku" %%% "boopickle" % boopickleVersion,
    "com.lihaoyi" %%% "scalatags" % scalatagsVersion,
    "com.lihaoyi" %%% "scalarx" % rxVersion,
    "org.openmole.scaladget" %%% "tools" % scaladgetVersion,
    "org.openmole.scaladget" %%% "svg" % scaladgetVersion,
    "org.openmole.scaladget" %%% "bootstrapnative" % scaladgetVersion,
    "org.scala-js" %%% "scalajs-dom" % scalajsDomVersion,
    "org.json4s" %% "json4s-jackson" % json4sVersion
  )
) dependsOn (shared)

lazy val server = project.in(file("server")) settings(
  organization := Organization,
  name := Name,
  version := Version,
  scalaVersion := ScalaVersion,
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
