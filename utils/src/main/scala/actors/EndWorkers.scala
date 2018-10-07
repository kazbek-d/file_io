package actors

import akka.actor.Props
import akka.routing.RoundRobinPool
import common.AkkaImplicits._

object EndWorkers {

  lazy val dbActor = system.actorOf(
    Props(new DbActor).withRouter(RoundRobinPool(nrOfInstances = 100)), "dbActor")

}
