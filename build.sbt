val Organization = "org.openmole"
val Name = "ScalaWUI"
val Version = "0.4"
val Scala2Version = "2.13.10"
val Scala3Version = "3.2.1"
val ScalaVersion = Scala3Version
val laminarVersion = "0.14.2"
val scaladgetVersion = "1.9.2"
val scalajsDomVersion = "2.0.0"
val scalatagsVersion = "0.11.1"
val supportedVersion = List(Scala3Version)

val endpoints4SVersion = "1.8.0+n"
val endpointCirceVersion = "2.2.0+n"


//Global / resolvers += Resolver.sonatypeRepo("staging")


lazy val shared = project.in(file("shared")) settings (
  scalaVersion := ScalaVersion,
  crossScalaVersions := supportedVersion,
  libraryDependencies ++= Seq(
    "org.openmole.endpoints4s" %%% "algebra" % endpoints4SVersion,
    "org.openmole.endpoints4s" %%% "json-schema-circe" % endpointCirceVersion,
    "io.circe" %% "circe-generic" % "0.14.3")
  ) enablePlugins (ScalaJSPlugin)

lazy val client = project.in(file("client")) enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin) settings(
  version := Version,
  scalaVersion := ScalaVersion,
  crossScalaVersions := supportedVersion,
  scalaJSUseMainModuleInitializer := false,
  webpackBundlingMode := BundlingMode.LibraryAndApplication(),
  webpackNodeArgs := Seq("--openssl-legacy-provider"),
  libraryDependencies ++= Seq(
    "com.raquo" %%% "laminar" % laminarVersion,
    "org.openmole.scaladget" %%% "tools" % scaladgetVersion,
    "org.openmole.scaladget" %%% "svg" % scaladgetVersion,
    "org.openmole.scaladget" %%% "bootstrapnative" % scaladgetVersion,
    "org.scala-js" %%% "scalajs-dom" % scalajsDomVersion,
    "org.openmole.endpoints4s" %%% "xhr-client" % "5.1.0+n"
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
    "org.openmole.endpoints4s" %% "http4s-server" % "10.0.0+n",
    "org.http4s" %% "http4s-blaze-server" % "0.23.12",
    "io.circe" %% "circe-parser" % "0.14.3"

    //    "org.endpoints4s" %% "akka-http-server" % "6.1.0+n",
//    "com.typesafe.akka" %% "akka-stream" % "2.6.18" //cross CrossVersion.for3Use2_13
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


