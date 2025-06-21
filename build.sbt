organization := "tv.cntt"
name         := "glokka"
version      := "3.0.0-SNAPSHOT"

//------------------------------------------------------------------------------

scalaVersion := "3.7.1"

javacOptions  ++= Seq("-source", "1.8", "-target", "1.8")
scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked")

//------------------------------------------------------------------------------

libraryDependencies += "com.typesafe.akka" %% "akka-actor"         % "2.8.8"
libraryDependencies += "com.typesafe.akka" %% "akka-cluster"       % "2.8.8"
libraryDependencies += "com.typesafe.akka" %% "akka-cluster-tools" % "2.8.8"

libraryDependencies += "org.specs2" %% "specs2-core" % "5.6.3" % "test"

//------------------------------------------------------------------------------

// For "sbt console", used while developing for cluster mode
Compile / unmanagedClasspath += { Attributed.blank(baseDirectory.value / "config_example") }

// Uncomment the following line to test in cluster mode (with only one node)
//Test / unmanagedClasspath += { Attributed.blank(baseDirectory.value / "config_example") }

//------------------------------------------------------------------------------

// Skip API doc generation to speedup "publishLocal" while developing.
// Comment out this line when publishing to Sonatype.
Compile / packageDoc / publishArtifact := false
