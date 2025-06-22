organization := "tv.cntt"
name         := "glekko"
version      := "1.0.0-SNAPSHOT"

//------------------------------------------------------------------------------

scalaVersion := "3.7.1"

javacOptions  ++= Seq("-source", "1.8", "-target", "1.8")
scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked")

//------------------------------------------------------------------------------

libraryDependencies += "org.apache.pekko" %% "pekko-actor"         % "1.1.3"
libraryDependencies += "org.apache.pekko" %% "pekko-cluster"       % "1.1.3"
libraryDependencies += "org.apache.pekko" %% "pekko-cluster-tools" % "1.1.3"

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
