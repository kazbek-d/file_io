import actors.FrontendClusterListener
import actors.file.FileActor
import akka.actor._
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.routing.RoundRobinPool
import com.typesafe.config.ConfigValueFactory
import common.Settings._
import graphql.{Repo, UserRepo}
import kamon.Kamon
import routs._

import scala.concurrent.Await
import scala.concurrent.duration.Duration


object WebServer extends App {

  println("WebServer Started. v.1.0.0")

  val config = getConfig
               .withValue("kamon.statsd.hostname", ConfigValueFactory.fromAnyRef(kamonStatsdHostname))
               .withValue("kamon.statsd.port", ConfigValueFactory.fromAnyRef(kamonStatsdPort))
  Kamon.start(config)

  import common.AkkaImplicits._
  val frontendActor = system.actorOf(Props(new FrontendClusterListener), actorFrontendName)
  val fileReceiveActor = system.actorOf(
    Props(new FileActor( frontendActor)).withRouter( RoundRobinPool( nrOfInstances = 100)), "fileReceiveActor")

  val am = new AkkaMonitoringRout(frontendActor)
  val ql = new QLRout(new Repo (frontendActor), new UserRepo (frontendActor) )
  val fsExt = new FileExtRout(frontendActor, fileReceiveActor)
  val fsSlowExt = new FileExtSlowRout(frontendActor)


  val route = respondWithHeaders ( WebServerCommon.headers ) {
    am.getRoute ~ fsExt.getRoute ~ fsSlowExt.getRoute ~ ql.getRoute
  }

  Http().bindAndHandle(route, webserverAddress, webserverPort)

  Await.result(system.whenTerminated, Duration.Inf)

}