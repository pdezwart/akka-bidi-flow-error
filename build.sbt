organization := "com.github.pdezwart"

name := "akka-bidi-flow-error"

version := "0.1"

scalaVersion := "2.12.12"

scalacOptions := Seq(
  "-Ywarn-unused:params",
  "-Ywarn-unused:locals",
  "-opt:l:inline"
)

resolvers += Resolver.mavenLocal

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "fr.davit" %% "akka-http-metrics-core" % "1.2.0",
  "fr.davit" %% "akka-http-metrics-prometheus" % "1.2.0",
  "com.typesafe.akka" %% "akka-actor" % "2.6.9",
  "com.typesafe.akka" %% "akka-http" % "10.2.1",
  "com.typesafe.akka" %% "akka-slf4j" % "2.6.9",
  "com.typesafe.akka" %% "akka-stream" % "2.6.9",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
)


