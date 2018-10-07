package graphql

import model.FileIO.{FileParams, FileParamsQuery}
import model.RestApi._

import scala.concurrent.Future

abstract class AbstractRepo {

  def getFileParamsBySecret(secret: String): Future[FileParamsResponse]

  def getFileParamsByProject(projectId: String): Future[FileParamsResponse]

  def getFileParamsByName(name: String): Future[FileParamsResponse]

  def getFileParams(fileParamsQuery: FileParamsQuery): Future[FileParamsResponse]

  def loadFileParamsBySecret(secretOpt: Option[String]): Future[Option[FileParams]]

}
