val Organization = "org.openmole"
val Name = "ScalaWUI"
val Version = "0.4"
val Scala2Version = "2.13.8"
val Scala3Version = "3.1.2"
val ScalaVersion = Scala3Version
val laminarVersion = "0.14.2"
val scaladgetVersion = "1.9.2"
val scalajsDomVersion = "2.0.0"
val scalatagsVersion = "0.11.1"
val supportedVersion = List(Scala2Version, Scala3Version)

val endpoints4SVersion = "1.7.0+n"

lazy val shared = project.in(file("shared")) settings (
  scalaVersion := ScalaVersion,
  crossScalaVersions := supportedVersion,
  libraryDependencies ++= Seq(
    "org.endpoints4s" %%% "algebra" % endpoints4SVersion,
    "org.endpoints4s" %%% "json-schema-generic" % endpoints4SVersion)
  ) enablePlugins (ScalaJSPlugin)

lazy val client = project.in(file("client")) enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin) settings(
  version := Version,
  scalaVersion := ScalaVersion,
  crossScalaVersions := supportedVersion,
  scalaJSUseMainModuleInitializer := false,
  webpackBundlingMode := BundlingMode.LibraryAndApplication(),
  libraryDependencies ++= Seq(
    "com.raquo" %%% "laminar" % laminarVersion,
    "org.openmole.scaladget" %%% "tools" % scaladgetVersion,
    "org.openmole.scaladget" %%% "svg" % scaladgetVersion,
    "org.openmole.scaladget" %%% "bootstrapnative" % scaladgetVersion,
    "org.scala-js" %%% "scalajs-dom" % scalajsDomVersion,
    "org.endpoints4s" %%% "xhr-client" % "5.0.0+n"
  )
) dependsOn (shared)

lazy val server = project.in(file("server")) settings(
  organization := Organization,
  name := Name,
  version := Version,
  scalaVersion := ScalaVersion,
  crossScalaVersions := supportedVersion,
  libraryDependencies ++= Seq(
    "com.lihaoyi" %% "scalatags" % scalatagsVersion,
    "org.endpoints4s" %% "akka-http-server" % "6.1.0+n",
    "com.typesafe.akka" %% "akka-stream" % "2.6.19" cross CrossVersion.for3Use2_13
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


