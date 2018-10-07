package routs

import java.nio.file.Paths

import akka.actor.ActorRef
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.stream.ActorAttributes
import akka.stream.scaladsl.FileIO.fromPath
import akka.util.Timeout
import common.AkkaImplicits._
import model.FileIO.FileParamsQuery
import model.RestApi.{FileParamsResponse, GetFileParams}

import scala.concurrent.duration._
import scala.util.{Failure, Success}

class FileExtSlowRout(frontendActor: ActorRef)  extends BaseRout {

  implicit val timeout: Timeout = 10.minute

  val getRoute = pathPrefix ( "protectfile" / Remaining ) { remaining =>

    // wget "http://localhost:8102/fileslow/__projectId__/__secret__"
    get {
      remaining.split ( "/" ).toList match {

        case projectId :: secret :: _ =>
          val getFileParams =
            (frontendActor ? GetFileParams ( FileParamsQuery ( Some ( secret ), None, None ) )).map {
              case fileParamsResponse: FileParamsResponse =>
                fileParamsResponse.fileParamsSeq.toList match {
                  case fileParams :: _ => fileParams.name.split ( '.' ).toList.reverse match {
                    case ext :: _ => ext
                    case _ => ""
                  }
                  case _ => ""
                }
              case _ => ""
            }

          onComplete ( getFileParams ) {

            case Success ( ext ) =>
              val path = Paths.get ( s"${common.Settings.filesystemPath}/$projectId/$secret" )
              val source = fromPath ( path )
                           .withAttributes ( ActorAttributes.dispatcher ( "akka.stream.default-blocking-io-dispatcher" ) )
              val file = path.toFile
              if (file.exists)
                complete ( HttpResponse ( entity = HttpEntity.Default (
                  ContentType ( MediaTypes.forExtension ( ext ), () => HttpCharsets.`UTF-8` ),
                  file.length,
                  source ) ) )
              else
                complete ( StatusCodes.NoContent, "File not fount" )

            case Failure ( ex ) =>
              complete ( StatusCodes.InternalServerError, s"An error occurred: ${ex.getMessage}" )

          }


        case _ => complete ( StatusCodes.BadRequest, "Not match to pattern .../fileslow/__projectId__/__secret__" )

      }


    }

  }

}



