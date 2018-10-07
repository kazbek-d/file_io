package graphql

import graphql.UserModel.QlUser

import scala.concurrent.Future

abstract class AbstractUserRepo {

  def authenticate(userName: String, password: String): Future[Option[String]]

  def authorise(token: String): Future[Option[QlUser]]

}
