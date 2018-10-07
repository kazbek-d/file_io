import akka.http.scaladsl.model.headers.RawHeader

object WebServerCommon {
  val headers = List (
    RawHeader ( "Access-Control-Allow-Origin", "*" ),
    RawHeader ( "Access-Control-Allow-Credentials", "true" ),
    RawHeader ( "Access-Control-Allow-Headers", "Authorization, Content-Type, X-Requested-With" )
  )
}
