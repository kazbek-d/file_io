import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import org.scalatest.FunSuite
import routs._

import scala.language.postfixOps


class ModelTest extends FunSuite {

  implicit val system = ActorSystem ( "FileIOClusterSystemModelTest" )
  implicit val materializer = ActorMaterializer ()
  implicit val executionContext = system.dispatcher

  val route = respondWithHeaders ( WebServerCommon.headers ) {
    new QLRout ( new RepoFake, new UserRepoFake ).getRoute
  }

  akka.http.scaladsl.Http ().bindAndHandle ( route, "localhost", 3003 )


  test ( "Get File Params By Secret. Check Code:200 and secret" ) {

    val query = " { \"query\": \"{  getFileParamsBySecret (\"secret\":\"XYZ\") {  secret  } }\", \"variables\":null, \"operationName\":null } "
    val responseBody = "{\"data\":{\"getFileParamsBySecret\":[{\"secret\":\"XYZ\"}]}}"

    import scalaj.http._
    val request: HttpRequest = Http ( "http://localhost:3003/graphql" )
                               .postData ( query )
                               .headers ( Map (
                                 "Accept" -> "application/json",
                                 "Content-Type" -> "application/json"
                               ) )
    val response = request.asString
    println ( query )
    println ( s"response: ${response}" )
    println ( s"response.body: ${response.body}" )
    println ( s"response.code: ${response.code}" )

    assert ( response.code === 200 )
    assert ( response.body === responseBody )
  }



}
