name := """hello-akka"""

version := "1.0"

// scalaVersion := "2.11.6"
scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.11",
"org.apache.kafka" % "kafka_2.10" % "0.8.1"
      exclude("javax.jms", "jms")
      exclude("com.sun.jdmk", "jmxtools")
      exclude("com.sun.jmx", "jmxri"),
  "com.typesafe.akka" %% "akka-testkit" % "2.3.11",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "junit" % "junit" % "4.12" % "test",
  "com.novocode" % "junit-interface" % "0.11" % "test",
  "com.typesafe.play" %% "play-json" % "2.3.9",
  "com.typesafe.play" %% "play-ws" % "2.3.9"
)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")
