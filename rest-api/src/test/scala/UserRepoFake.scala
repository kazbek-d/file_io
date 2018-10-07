import java.util.UUID

import graphql.AbstractUserRepo
import graphql.UserModel.QlUser
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserRepoFake extends AbstractUserRepo {
  var tokens = Map.empty [String, QlUser]

  override def authenticate(userName: String, password: String): Future[Option[String]] = Future {
    if (userName == "root" && password == "temppwd") {
      val token = UUID.randomUUID ().toString
      tokens = tokens + (token → QlUser ( "admin", "VIEW_PERMISSIONS" :: "EDIT_COLORS" :: "VIEW_COLORS" :: Nil ))
      Some ( token )
    } else if (userName == "john" && password == "apples") {
      val token = UUID.randomUUID ().toString
      tokens = tokens + (token → QlUser ( "john", "VIEW_COLORS" :: Nil ))
      Some ( token )
    } else None
  }

  override def authorise(token: String): Future[Option[QlUser]] = Future ( tokens.get ( token ) )
}