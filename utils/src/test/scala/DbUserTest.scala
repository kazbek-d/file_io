//import java.util.UUID
//
//import data.CassandraRepositoryJavaDriver
//import model._
//import org.joda.time.DateTime
//
//object DbUserTest extends App {
//
//  val cassy = new CassandraRepositoryJavaDriver
//
//  val userName = "root"
//  val userPassword = "temppwd"
//  val userPermissions = "ADMIN,VIEW_PERMISSIONS"
//  val token = UUID.randomUUID
//  val tokenCreatedOn = DateTime.now
//
//  val dbUser =
//    cassy.getDbUser ( userName ).getOrElse {
//      cassy.setDbUser ( DbUser ( userName, userPassword, userPermissions, token, tokenCreatedOn ) )
//      cassy.setDbUserToken ( DbUserToken ( token, userName, userPermissions, tokenCreatedOn ) )
//      cassy.getDbUser ( userName ).get
//    }
//
//  println ( dbUser )
//  println ( cassy.getDbUserToken ( dbUser.token ).get )
//
//}
