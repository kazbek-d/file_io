import akka.actor._

import scala.concurrent.Await
import scala.concurrent.duration.Duration


/** How to Run from bash

export AKKA_SEED_NODES = '0.0.0.0:2551,0.0.0.0:2552'
export AKKA_REMOTING_BIND_HOST = "0.0.0.0"
export AKKA_REMOTING_BIND_PORT = 2551
export AKKA_ACTOR_SYSTEM_NAME = 'LeomaxClusterSystem'


sbt \
-Dakka.actor.provider=cluster \
-Dakka.remote.netty.tcp.hostname="$(eval "echo $AKKA_REMOTING_BIND_HOST")" \
-Dakka.remote.netty.tcp.port="$AKKA_REMOTING_BIND_PORT" $(IFS=','; I=0; for NODE in $AKKA_SEED_NODES; do echo " \
-Dakka.cluster.seed-nodes.$I=akka.tcp://$AKKA_ACTOR_SYSTEM_NAME@$NODE"; I=$(expr $I + 1); done) \
-DactorSystemName=${AKKA_ACTOR_SYSTEM_NAME} \
run
  */

// sbt run java -Dcom.sun.management.jmxremote.port=9999 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false
// ./akka-cluster 127.0.0.1 9999 cluster-status
// akka-cluster 127.0.0.1 9999 down akka.tcp://FileIOClusterSystem@127.0.0.1:49603
// sbt aspectj-runner:run -Dcom.sun.management.jmxremote.port=9999 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false
// java -Dcom.sun.management.jmxremote.port=9999 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -jar backend-workers-assembly-1.0.jar


object Kernel extends App {
  
  println("Kernel Started. v.1.0.0")

  val actorSystemName =
    sys.props.getOrElse(
      "actorSystemName",
      throw new IllegalArgumentException("Actor system name must be defined by the actorSystemName property")
    )

  val actorSystem = ActorSystem(actorSystemName)

  Await.result(actorSystem.whenTerminated, Duration.Inf)

}
