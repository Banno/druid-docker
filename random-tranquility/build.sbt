organization := "com.banno"

name := "random-tranquility"

version := "0.1.0"

scalaVersion := "2.10.4"

resolvers ++= Seq(
  "Metamarkets" at "https://metamx.artifactoryonline.com/metamx/pub-libs-releases-local/",
  "clojars" at "http://clojars.org/repo/"
)

libraryDependencies ++= Seq(
  "com.metamx" %% "tranquility" % "0.3.4",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.4.4",
  "com.metamx" %% "scala-util" % "1.8.43" exclude("log4j", "log4j") force(),
  "ch.qos.logback" % "logback-classic" % "1.1.2"
)
