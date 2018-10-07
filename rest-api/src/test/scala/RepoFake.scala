import java.util.UUID

import graphql.AbstractRepo
import model.FileIO.FileParams
import model._
import org.joda.time.DateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RepoFake extends AbstractRepo {

  override def getFileParamsBySecret(secret: String): Future[RestApi.FileParamsResponse] = Future (
    RestApi.FileParamsResponse(Seq(FileParams("XYZ", "", "", 0, "")))
  )

  override def getFileParamsByProject(projectId: String): Future[RestApi.FileParamsResponse] = ???

  override def getFileParamsByName(name: String): Future[RestApi.FileParamsResponse] = ???

  override def getFileParams(fileParamsQuery: FileIO.FileParamsQuery): Future[RestApi.FileParamsResponse] = ???

  override def loadFileParamsBySecret(secretOpt: Option[String]): Future[Option[FileParams]] = ???
}