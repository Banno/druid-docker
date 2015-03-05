import DockerKeys._
import sbtdocker.mutable.Dockerfile

organization := "com.banno"

name := "twitter-tranquility"

version := "0.1.0"

scalaVersion := "2.10.4"

resolvers ++= Seq(
  "bintray-banno-oss-releases" at "http://dl.bintray.com/banno/oss",
  "Metamarkets" at "https://metamx.artifactoryonline.com/metamx/pub-libs-releases-local/",
  "clojars" at "http://clojars.org/repo/")

libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-clients" % "0.8.2.0",
  "org.twitter4j" % "twitter4j-stream" % "4.0.2",
  "com.metamx" %% "tranquility" % "0.3.1",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.4.4", //magic
  "com.metamx" %% "scala-util" % "1.8.43" exclude("log4j", "log4j") force(), //magic
  // "jsonz" %% "jsonz" % "1.0.0",
  "com.typesafe" % "config" % "1.2.1",
  "ch.qos.logback" % "logback-classic" % "1.1.2"
)

dockerSettings

dockerfile in docker := {
  val jarFile = artifactPath.in(Compile, packageBin).value
  val classpath = (managedClasspath in Compile).value
  val mainclass = mainClass.in(Compile, packageBin).value.getOrElse(sys.error("Expected exactly one main class"))
  val jarTarget = s"/app/${jarFile.getName}"
  // Make a colon separated classpath with the JAR file
  val classpathString = classpath.files.map("/app/" + _.getName)
    .mkString(":") + ":" + jarTarget
  new Dockerfile {
    // Base image
    from("dockerfile/java")
    // Add all files on the classpath
    classpath.files.foreach { file =>
      add(file, "/app/")
    }
    // Add the JAR file
    add(jarFile, jarTarget)
    // On launch run Java with the classpath and the main class
    entryPoint("java", "-cp", classpathString, mainclass)
  }
}

net.virtualvoid.sbt.graph.Plugin.graphSettings
