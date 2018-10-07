package common

import java.util.UUID

import scala.concurrent.{ExecutionContext, Future}

object Helpers {
  
  /**
    * Convert Option[Future[T]] to Future[Option[T]]
    */
  def OptFuture_Revert[A](x: Option[Future[A]])(implicit ec: ExecutionContext): Future[Option[A]] =
    x match {
      case Some ( f ) => f.map ( Some ( _ ) )
      case None => Future.successful ( None )
    }



  
  def stringToSeq(string: String): Seq[UUID] = {
    if (string.length == 0) Seq.empty
    else {
      try {
        string.split ( "," ).map ( s => UUID.fromString ( s ) ).toSeq
      } catch {
        case _: Throwable => Seq.empty
      }
    }
  }


}
