val Organization = "org.openmole"
val Name = "ScalaWUI"
val Version = "0.4"
val ScalaVersion = "2.13.7"
val laminarVersion = "0.12.2"
val scaladgetVersion = "1.9.2"
val scalajsDomVersion = "2.0.0"
val scalatagsVersion = "0.9.4"

lazy val shared = project.in(file("shared")) settings (
  scalaVersion := ScalaVersion,
  libraryDependencies ++= Seq(
    "org.endpoints4s" %%% "algebra" % "1.7.0",
    "org.endpoints4s" %%% "json-schema-generic" % "1.7.0")
  ) enablePlugins (ScalaJSPlugin)

lazy val client = project.in(file("client")) enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin) settings(
  version := Version,
  scalaVersion := ScalaVersion,
  scalaJSUseMainModuleInitializer := false,
  webpackBundlingMode := BundlingMode.LibraryAndApplication(),
  libraryDependencies ++= Seq(
    "com.raquo" %%% "laminar" % laminarVersion,
    "org.openmole.scaladget" %%% "tools" % scaladgetVersion,
    "org.openmole.scaladget" %%% "svg" % scaladgetVersion,
    "org.openmole.scaladget" %%% "bootstrapnative" % scaladgetVersion,
    "org.scala-js" %%% "scalajs-dom" % scalajsDomVersion,
    "org.endpoints4s" %%% "xhr-client" % "5.0.0"
  )
) dependsOn (shared)

lazy val server = project.in(file("server")) settings(
  organization := Organization,
  name := Name,
  version := Version,
  scalaVersion := ScalaVersion,
  libraryDependencies ++= Seq(
    "com.lihaoyi" %% "scalatags" % scalatagsVersion,
    "org.endpoints4s" %% "akka-http-server" % "6.1.0",
    "com.typesafe.akka" %% "akka-stream" % "2.6.15"
  ),

  Compile / compile := {
    val jsBuild = (client / Compile / fullOptJS / webpack).value.head.data

    val demoTarget = target.value
    val demoResource = (client / Compile / resourceDirectory).value

    IO.copyFile(jsBuild, demoTarget / "webapp/js/demo.js")
    IO.copyDirectory(demoResource, demoTarget)
    (Compile / compile).value
  }
) dependsOn (shared)


//lazy val bootstrap = project.in(file("target/bootstrap")) enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin) settings(
//  version := Version,
//  scalaVersion := ScalaVersion,
//
//) dependsOn(client, server)
