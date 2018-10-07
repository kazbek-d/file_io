package common

import model.FileIO.{FileParamsQuery, FileParamsRest}
import model.RestApi._
import spray.json.DefaultJsonProtocol._

object RestApiImplicits {

  implicit val anyErrFormat = jsonFormat1(AnyErr)
  implicit val SinkParamsFormat = jsonFormat3(SinkParams)
  implicit val SinkTasksFormat = jsonFormat1(SinkTasks)
  implicit val FileParamsFormat = jsonFormat7(FileParamsRest)
  implicit val FileParamsQueryFormat = jsonFormat3(FileParamsQuery)
  implicit val ExecutorsGroupFormat =  jsonFormat5(ExecutorsGroup)
  implicit val MonitorRsFormat =  jsonFormat2(MonitorRs)


}
