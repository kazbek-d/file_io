import com.typesafe.sbt.packager.docker._

name := "rest-api"

organization := "com.file_io"

version := "1.0"

scalaVersion := "2.12.4"

scalacOptions += "-Ypartial-unification"

val akkaVersion = "2.5.6"
val akkaHttpVersion = "10.0.10"
val kamon = "0.6.7"

lazy val common = RootProject(file("../common"))
val main = Project(id = "rest-api", base = file("."))
   .dependsOn(common)
   .enablePlugins(JavaAppPackaging)
   .settings(
     dockerEntrypoint ++= Seq(
       """-DactorSystemName="$(eval "echo $AKKA_ACTOR_SYSTEM_NAME")"""",
       """-Dakka.remote.netty.tcp.hostname="$(eval "echo $AKKA_REMOTING_BIND_HOST")"""",
       """-Dakka.remote.netty.tcp.port="$AKKA_REMOTING_BIND_PORT"""",
       """$(IFS=','; I=0; for NODE in $AKKA_SEED_NODES; do echo "-Dakka.cluster.seed-nodes.$I=akka.tcp://$AKKA_ACTOR_SYSTEM_NAME@$NODE"; I=$(expr $I + 1); done)""",
       "-Dakka.io.dns.resolver=async-dns",
       "-Dakka.io.dns.async-dns.resolve-srv=true",
       "-Dakka.io.dns.async-dns.resolv-conf=on"
     )
   )
   .settings(
     dockerCommands :=
       dockerCommands.value.flatMap {
         case ExecCmd("ENTRYPOINT", args @ _*) => Seq(Cmd("ENTRYPOINT", args.mkString(" ")))
         case v => Seq(v)
       }
   )
   .settings(
     dockerUpdateLatest := true
   )
   .settings(
     dockerBaseImage := "local/openjdk-jre-8-bash"
   )

// CATS
libraryDependencies += "org.typelevel" %% "cats-core" % "1.0.0-MF"

libraryDependencies ++= Seq(
  // Akka
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-agent" % akkaVersion,
  "com.typesafe.akka" %% "akka-camel" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-metrics" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
  "com.typesafe.akka" %% "akka-contrib" % akkaVersion,
  "com.typesafe.akka" %% "akka-multi-node-testkit" % akkaVersion,
  "com.typesafe.akka" %% "akka-osgi" % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence-tck" % akkaVersion,
  "com.typesafe.akka" %% "akka-remote" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "com.typesafe.akka" %% "akka-distributed-data" % akkaVersion,
  "com.typesafe.akka" %% "akka-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-distributed-data" % akkaVersion,

  // Akka Http
  "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-jackson" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-xml" % akkaHttpVersion,

  // Sangria
  "org.sangria-graphql" %% "sangria" % "1.4.0",
  "org.sangria-graphql" %% "sangria-spray-json" % "1.0.0",


  // Kamon
  "io.kamon" %% "kamon-core" % kamon,
  "io.kamon" %% "kamon-statsd" % kamon,
  "io.kamon" %% "kamon-datadog" % kamon,

  // Simplified Http
  "org.scalaj" %% "scalaj-http" % "2.3.0"
)

resolvers += "Akka Snapshots" at "http://repo.akka.io/snapshots/"

mainClass in Compile := Some("WebServer")