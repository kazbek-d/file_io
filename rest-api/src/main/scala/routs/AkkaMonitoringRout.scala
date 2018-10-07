package routs

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.util.Timeout
import common.AkkaImplicits._
import model.RestApi._

import scala.concurrent.Future
import scala.concurrent.duration._

class AkkaMonitoringRout(actorRef: ActorRef)  extends BaseRout {

  implicit val timeout: Timeout = 10.seconds


  val getRoute = pathPrefix ( "monitor" ) {

    get {

      onComplete {
        (actorRef ? MonitorRq).map {
          case ExecutorsRefsGroup ( executorsRefsSeq ) =>

            executorsRefsSeq.map { executorsRefs =>

              Future.sequence ( executorsRefs.actorRefs.map { actorRef =>
                (actorRef ? MonitorPing).map ( {
                  case MonitorPong => true
                  case _ => false
                } )

              } ).map { seq =>
                ExecutorsGroup (
                  executorsRefs.executorType,
                  executorsRefs.actorRefs.length == seq.count ( x => x ),
                  executorsRefs.actorRefs.length,
                  seq.count ( x => x ),
                  seq.count ( !_ ) )
              }

            }

        }.map ( Future.sequence ( _ ) ).flatten.map { seq =>
          MonitorRs ( seq, seq.forall ( _.isOk ) )
        }
      }( makeResult )

    }

  }
}