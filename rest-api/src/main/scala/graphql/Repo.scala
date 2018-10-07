package graphql

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import common.AkkaImplicits._
import model.FileIO.{FileParams, FileParamsQuery}
import model.RestApi.{FileParamsResponse, _}

import scala.concurrent.Future
import scala.concurrent.duration._


class Repo(actorRef: ActorRef) extends AbstractRepo {


  val timeout10Minutes = 10.minute
  implicit val timeout: Timeout = timeout10Minutes


  def getFileParamsBySecret(secret: String): Future[FileParamsResponse] =
    getFileParams ( FileParamsQuery ( Some ( secret ), None, None ) )

  def getFileParamsByProject(projectId: String): Future[FileParamsResponse] =
    getFileParams ( FileParamsQuery ( None, Some ( projectId ), None ) )

  def getFileParamsByName(name: String): Future[FileParamsResponse] =
    getFileParams ( FileParamsQuery ( None, None, Some ( name ) ) )

  def getFileParams(fileParamsQuery: FileParamsQuery): Future[FileParamsResponse] =
    (actorRef ? GetFileParams ( fileParamsQuery )).map {
      case fileParamsResponse: FileParamsResponse => fileParamsResponse
      case ex@_ => throw new CustomExceptions.FileIOError ( ex.toString )
    }


  def loadFileParamsBySecret(secretOpt: Option[String]): Future[Option[FileParams]] = secretOpt match {
    case Some ( secret ) => getFileParamsBySecret ( secret ).map ( _.fileParamsSeq.headOption )
    case None => Future ( None )
  }



}
