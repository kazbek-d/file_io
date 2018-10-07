package actors

import akka.actor.{Actor, ActorLogging}
import akka.util.Timeout
import common.SparkImplicits.repositoryCassyJavaDriver
import kamon.trace.Tracer
import model.RestApi._

import scala.concurrent.duration._

class DbActor extends Actor with ActorLogging {

  implicit val timeout: Timeout = 1.minute

  override def receive: Receive = {

    case DbUtilsJob ( actorRef, SetFileParams ( fileParams ) ) =>
      Tracer.withNewContext ( "SetFileParams comes", autoFinish = true ) {
        log.info ( "SetFileParams comes." )
        try {
          repositoryCassyJavaDriver.setFileParams ( fileParams )
          actorRef ! fileParams
        } catch {
          case e: Exception =>
            log.info ( e.getMessage )
            actorRef ! AnyErr ( e.getMessage )
        }
      }

    case DbUtilsJob ( actorRef, GetFileParams ( fileParamsQuery ) ) =>
      Tracer.withNewContext ( "GetFileParams comes", autoFinish = true ) {
        log.info ( "GetFileParams comes." )
        actorRef ! FileParamsResponse ( repositoryCassyJavaDriver.getFileParams ( fileParamsQuery ) )
      }

    case DbUtilsJob ( actorRef, RqDbUser ( login, password ) ) =>
      Tracer.withNewContext ( "RqDbUser comes", autoFinish = true ) {
        log.info ( "RqDbUser comes." )
        actorRef ! RsDbUser ( repositoryCassyJavaDriver.getDbUserAndUpdateToken ( login, password ) )
      }

    case DbUtilsJob ( actorRef, RqDbUserToken ( token ) ) =>
      Tracer.withNewContext ( "RqDbUserToken comes", autoFinish = true ) {
        log.info ( "RqDbUserToken comes." )
        actorRef ! RsDbUserToken ( repositoryCassyJavaDriver.getDbUserToken ( token ) )
      }

    case _ =>
      Tracer.withNewContext ( "Something comes", autoFinish = true ) {
        log.info ( "Something comes." )
      }
  }

}