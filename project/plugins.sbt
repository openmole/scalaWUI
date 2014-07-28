resolvers ++= Seq(Resolver.sonatypeRepo("snapshots"),
  Resolver.url("scala-js-releases",
    url("http://dl.bintray.com/content/scala-js/scala-js-releases"))(
      Resolver.ivyStylePatterns))

resolvers += "Typesafe repository" at
  "http://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("fr.iscpif" %% "jsmanager" % "0.1.0-SNAPSHOT")

addSbtPlugin("org.scalatra.sbt" % "scalatra-sbt" % "0.3.5")