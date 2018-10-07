package model

import java.util.UUID

import akka.actor.ActorRef
import model.FileIO.{FileParams, FileParamsQuery}

object RestApi {

  trait Action

  case object Start extends Action

  case object Stop extends Action

  case object GetTasks extends Action


  case object MonitorRq

  case object MonitorPing

  case object MonitorPong

  case class ExecutorsRefs(executorType: String, actorRefs: Seq[ActorRef])

  case class ExecutorsRefsGroup(executorsRefsSeq: Seq[ExecutorsRefs])

  case class ExecutorsGroup(executorType: String, isOk: Boolean, executorsCount: Int, okCount: Int, errCount: Int)


  trait Requests

  case class SinkParams(kafkaTopic: String, cassandraKeyspace: String, cassandraTable: String) extends Requests

  trait SinkTask {
    def getDescription: String
  }

  // TODO: Use Generic (t: SinkableType)
  case class SinkTaskKafkaCassandra(t: SinkableType, params: SinkParams) extends SinkTask {
    override def getDescription: String = s"SinkableType: $t, SinkParams: $params"
  }

  trait SinkRequests extends Requests

  case class Sink(action: Action, sinkTask: Option[SinkTask] = None) extends SinkRequests

  case class SinkJob(actorRef: ActorRef, sink: SinkRequests) extends SinkRequests


  trait DbRequests extends Requests

  // BackEnd
  case class DbRequestData(actorRef: ActorRef, request: DbRequests)

  trait DbLoyaltyRequests extends DbRequests

  case class DbJob(actorRef: ActorRef, requestBody: DbRequests) extends DbRequests

  trait DbLotRequests extends DbRequests // Gtk, Lot, LotProps and etc..

  trait ProxyAction extends Requests // Create Order
  case class ProxyJob(actorRef: ActorRef, requestBody: ProxyAction) extends ProxyAction


  // Utils
  trait DbUtilsRequests extends DbRequests

  case class GetFileNamesQL(queryText: String) extends DbUtilsRequests

  case class GetFileNameByHash(hash: String) extends DbUtilsRequests

  case class GetFileHashByName(fileName: String) extends DbUtilsRequests

  case class RqDbUser(login: String, password: String) extends DbUtilsRequests

  case class RqDbUserToken(token: UUID) extends DbUtilsRequests

  case class SetFileParams(fileParams: FileParams) extends DbUtilsRequests

  case class GetFileParams(fileParamsQuery: FileParamsQuery) extends DbUtilsRequests

  case class DbUtilsJob(actorRef: ActorRef, requestBody: DbUtilsRequests) extends DbUtilsRequests


  trait UtilsRequests extends Requests

  case class UtilsJob(actorRef: ActorRef, requestBody: UtilsRequests) extends UtilsRequests

  trait FileSenderRequests extends UtilsRequests

  case class FileSenderTask(fileSenderActor: ActorRef, fileName: String) extends FileSenderRequests

  case class FileSenderJob(actorRef: ActorRef, fileSenderTask: FileSenderTask) extends FileSenderRequests

  trait StreamRequests extends UtilsRequests

  case class StreamTask(backpressuredActorRef: Option[ActorRef], user: UUID, fileName: String, folderName: String, destinationType: Int) extends StreamRequests

  case class StreamJob(actorRef: ActorRef, streamTask: StreamTask) extends StreamRequests


  trait Responces

  case class MonitorRs(executorsGroups: Seq[ExecutorsGroup], isOk: Boolean) extends Responces

  trait Err extends Responces

  case object Ok extends Responces

  case object UnhandledDbTask extends Err

  case class AnyErr(message: String) extends Err


  trait DBResponces extends Responces

  case class FileParamsResponse(fileParamsSeq: Seq[FileParams]) extends DBResponces

  case class RsDbUser(dbUser: Option[DbUser]) extends DBResponces

  case class RsDbUserToken(dbUserToken: Option[DbUserToken]) extends DBResponces

  case class RsDel(id: Option[UUID]) extends DBResponces

  trait SinkTaskResponces

  case object UnhandledSinkTask extends Err

  case class SinkTasks(tasksDescription: Seq[String]) extends SinkTaskResponces

  case object SinkTasksAlreadyInProgress extends SinkTaskResponces

  trait StreamTaskResponces

  case object Ping extends StreamTaskResponces

  case object ChunkAccepted extends StreamTaskResponces

  case object ChunkDenied extends StreamTaskResponces

}