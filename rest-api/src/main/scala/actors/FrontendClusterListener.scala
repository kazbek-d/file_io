package actors

import akka.actor.{Actor, ActorLogging, ActorRef}
import model.AkkaObjects.UtilsRegistration
import model.RestApi._


class FrontendClusterListener extends Actor with ActorLogging {

  var backendJobCounter = 0

  var utilsJobCounter = 0
  var utils = IndexedSeq.empty [ActorRef]

  override def receive = {

    case UtilsRegistration if !utils.contains ( sender () ) =>
      context watch sender ()
      utils = utils :+ sender ()

    case MonitorRq =>
      sender ! ExecutorsRefsGroup(Seq(
        ExecutorsRefs("utils", utils.toList)
      ))


    // Utils Requests
    case _: UtilsRequests if utils.isEmpty =>
      sender ! AnyErr ( "Utils Nodes are unavailable, try again later" )
    case _: DbUtilsRequests if utils.isEmpty =>
      sender ! AnyErr ( "Utils Nodes are unavailable, try again later" )

    case request: UtilsRequests =>
      utilsJobCounter += 1
      utils ( utilsJobCounter % utils.size ) forward UtilsJob ( sender, request )

    case request: DbUtilsRequests =>
      utilsJobCounter += 1
      utils ( utilsJobCounter % utils.size ) forward DbUtilsJob ( sender, request )


  }
}
