name := """play-typed-actors-scala"""
organization := "com.lightbend"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.6"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test

// https://mvnrepository.com/artifact/com.typesafe.akka/akka-actor-typed
libraryDependencies += "com.typesafe.akka" %% "akka-actor-typed" % "2.5.17"

