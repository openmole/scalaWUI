resolvers ++= Seq(Resolver.url("scala-js-releases",
  url("http://dl.bintray.com/content/scala-js/scala-js-releases"))(
    Resolver.ivyStylePatterns))

resolvers += "Typesafe repository" at
  "http://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("fr.iscpif" %% "jsmanager" % "0.6.0")

addSbtPlugin("org.scalatra.sbt" % "scalatra-sbt" % "0.3.5")
