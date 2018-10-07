import actors.BackendClusterListener
import akka.actor._
import com.typesafe.config.ConfigValueFactory
import common.Settings._
import kamon.Kamon

import scala.concurrent.Await
import scala.concurrent.duration.Duration


object Utils extends App {

  println("Utils Started. v.1.0.0")

  val config = getConfig
      .withValue("kamon.statsd.hostname", ConfigValueFactory.fromAnyRef(kamonStatsdHostname))
      .withValue("kamon.statsd.port", ConfigValueFactory.fromAnyRef(kamonStatsdPort))
  Kamon.start(config)

  import common.AkkaImplicits._

  system.actorOf(Props[BackendClusterListener], actorBackendName)

  Await.result(system.whenTerminated, Duration.Inf)

}