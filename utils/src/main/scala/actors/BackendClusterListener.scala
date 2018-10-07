package actors

import actors.EndWorkers._
import akka.actor.{Actor, ActorLogging, RootActorPath}
import akka.cluster.ClusterEvent._
import akka.cluster.{Cluster, Member, MemberStatus}
import common.Settings._
import model.AkkaObjects.UtilsRegistration
import model.RestApi._

class BackendClusterListener extends Actor with ActorLogging {

  val cluster = Cluster(context.system)

  // subscribe to cluster changes, re-subscribe when restart
  override def preStart(): Unit = cluster.subscribe(self, classOf[MemberUp])

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive = {

    case state: CurrentClusterState =>
      state.members.filter(_.status == MemberStatus.Up) foreach register

    case MemberUp(member) =>
      log.info(s"[Listener] node is up: $member")
      register(member)


    case MonitorPing =>
      sender ! MonitorPong


    case request: DbUtilsJob =>
      log.info(s"DbUtilsJob job comes.")
      dbActor ! request



    case request: UtilsJob =>
      log.info(s"UtilsJob job comes.")
      request match {                 

        case _ =>
          log.info(s"Unknown job comes.")

      }
  }

  def register(member: Member): Unit =
    if (member.hasRole("frontend"))
      context.actorSelection(RootActorPath(member.address) / "user" / actorFrontendName) !
        UtilsRegistration

}
