package common

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

object AkkaImplicits {

  val actorSystemName =
    sys.props.getOrElse(
      "actorSystemName",
      throw new IllegalArgumentException("Actor system name must be defined by the actorSystemName property")
    )

  implicit val system = ActorSystem(actorSystemName)
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

}