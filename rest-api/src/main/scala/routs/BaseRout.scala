package routs

import akka.Done
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Route, StandardRoute}
import model.FileIO.{FileParams, FileParamsRest}

import scala.util.{Failure, Success, Try}
import model.RestApi._
import common.Settings.fileIOprefix

trait BaseRout {

  import common.RestApiImplicits._

  protected val makeResult: (Try[Any]) => StandardRoute = {
    case Success(value) => value match {
      case err: AnyErr => complete(StatusCodes.BadRequest, err)

      case response : MonitorRs =>
        if (response.isOk) complete(StatusCodes.OK, response)
        else complete(StatusCodes.InternalServerError, response)

      case sinkTasks : SinkTasks => complete(StatusCodes.Accepted, sinkTasks)
      case SinkTasksAlreadyInProgress => complete(StatusCodes.Accepted, SinkTasksAlreadyInProgress.toString)

      case fileParams : FileParams => complete(StatusCodes.Accepted, fileParams.toFileParamsRest.copy(
        url = s"$fileIOprefix/file/${fileParams.projectId}/${fileParams.secret}",
        urlProtect = s"$fileIOprefix/protectfile/${fileParams.projectId}/${fileParams.secret}"))
      case fileParamsRest : FileParamsRest => complete(StatusCodes.Accepted, fileParamsRest)


      case Done => complete(StatusCodes.Accepted, "Done")

      case _ => complete(StatusCodes.Accepted, "Ok")
    }
    case Failure(ex) =>
      complete(StatusCodes.InternalServerError, s"An error occurred: ${ex.getMessage}")
  }

  def getRoute: Route

}
