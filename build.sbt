
name := """connect-four-webtech"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

resolvers += Resolver.sonatypeRepo("snapshots")

//scalaVersion := "2.12.3"
scalaVersion := "2.11.4"

libraryDependencies += guice

libraryDependencies += "org.webjars" %% "webjars-play" % "2.4.0"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test

herokuAppName in Compile := "connect-four-13135"


