package routs

import java.nio.file.Paths

import akka.actor.ActorRef
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{BasicHttpCredentials, OAuth2BearerToken}
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.stream.ActorAttributes
import akka.stream.scaladsl.FileIO.fromPath
import akka.util.Timeout
import graphql.UserRepo
import model.FileIO.FileUpload
import scala.concurrent.Future
import scala.concurrent.duration._
import common.AkkaImplicits.executionContext

class FileExtRout(frontendActor: ActorRef, fileReceiveActor: ActorRef)  extends BaseRout {

  implicit val timeout: Timeout = 10.minute

  val userRepo = new UserRepo ( frontendActor )

  val getRoute = pathPrefix ( "file" / Remaining ) { remaining =>

    // wget "http://localhost:8102/file/__projectId__/__secret__"
    get {
      val path = Paths.get ( s"${common.Settings.filesystemPath}/$remaining" )
      val source = fromPath ( path )
                   .withAttributes ( ActorAttributes.dispatcher ( "akka.stream.default-blocking-io-dispatcher" ) )
      val file = path.toFile
      if (file.exists)
        complete ( HttpResponse ( entity = HttpEntity.Default ( ContentTypes.`application/octet-stream`, file.length, source ) ) )
      else
        complete ( StatusCodes.NoContent, "File not fount" )
    } ~
      extractCredentials { httpCredentials =>
        post {

          def upload(isAuth: Future[Boolean]) = {
            // curl --form "data=@fileName" http://localhost:8102/file/__projectId__  -H "Authorization: Bearer SECRET_TOKEN"
            // curl --form "data=@fileName" http://localhost:8102/file/__projectId__  -u john:doe
            // curl -X POST 'http://localhost:8102/file/__projectId__' -F "data=@__name_in_file_system__;filename=__desirable_name__" -H "Authorization: Bearer SECRET_TOKEN"
            // curl -X POST 'http://localhost:8102/file/__projectId__' -F "data=@__name_in_file_system__;filename=__desirable_name__" -u john:doe
            uploadedFile ( "data" ) {
              case (fileInfo, file) =>
                onComplete {
                  for {
                    isOk <- isAuth if isOk
                    result <- fileReceiveActor ? FileUpload ( projectId = remaining, fileInfo, file )
                  } yield {
                    result
                  }
                }( makeResult )
            }
          }

          httpCredentials match {
            //  Basic Authorization
            case Some ( BasicHttpCredentials ( user, pass ) ) =>
                upload(userRepo.authenticate ( user, pass ).map(_.isDefined))

            // Authorization: Bearer SECURITY_TOKEN
            case Some ( OAuth2BearerToken ( token: String ) ) =>
              upload(userRepo.authorise(token).map(_.isDefined))

            case _ => complete ( HttpResponse ( 401, entity = "Unauthorized" ) )
          }
        }
      }
  }

}



