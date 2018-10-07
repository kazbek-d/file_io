package actors.file

import java.nio.file.{Files, Paths}

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.http.scaladsl.model.{ContentType, HttpCharsets, MediaTypes}
import akka.stream.ActorAttributes
import akka.stream.scaladsl.FileIO.{fromPath, toPath}
import akka.util.Timeout
import common.AkkaImplicits._
import model.FileIO
import model.FileIO.{FileParams, FileUpload}
import model.RestApi.{AnyErr, SetFileParams}
import akka.pattern.ask

import scala.util.{Failure, Success}
import scala.concurrent.duration._


class FileActor(frontendActor: ActorRef) extends Actor with ActorLogging {

  implicit val timeout: Timeout = 1.minute
  
  override def receive: Receive = {

    case FileUpload(projectId, fileInfo, tempFile) =>
      log.info(s"File comes.")

      val senderRef = sender

      val contentType = ContentType ( MediaTypes.forExtension ( fileInfo.fileName.split ( '.' ).toList.reverse match {
        case ext :: _ => ext
        case _ => ""
      } ), () => HttpCharsets.`UTF-8` )

      val fileParams = FileParams(
        FileIO.generateHash, projectId, fileInfo.fileName, tempFile.length, contentType.toString )

      val dir = Paths.get(s"${common.Settings.filesystemPath}/$projectId")
      Files.createDirectories(dir)
      val path = Paths.get(s"$dir/${fileParams.secret}")
      val source = fromPath(tempFile.toPath)
      val sink = toPath(path)
      source
      .withAttributes(ActorAttributes.dispatcher("akka.stream.default-blocking-io-dispatcher"))
      .to(sink)
      .run()
      .onComplete {
        case Success(value) => value.status match {
          case Failure(exception) =>
            senderRef ! AnyErr(exception.getMessage) // Send Err to RestActor
          case Success(_) =>

            log.info(s"FileParams: $fileParams.")

            (frontendActor ? SetFileParams(fileParams)).onComplete {
              case Success(dbAnswer) =>
                senderRef ! dbAnswer  // Send FileParams to RestActor
              case Failure(ex) =>
                senderRef ! AnyErr(ex.getMessage) // Send Err to RestActor
            }
        }
        case Failure(ex) =>
          senderRef ! AnyErr(ex.getMessage) // Send Err to RestActor
      }

      log.info(s"sender ! fileParams.")
      

  }

}
