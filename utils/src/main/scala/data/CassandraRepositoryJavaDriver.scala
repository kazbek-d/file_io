package data

import java.lang.System.nanoTime
import java.util.UUID

import com.datastax.driver.core._
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.datastax.driver.dse.auth.DsePlainTextAuthProvider
import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}
import common.Settings._
import kamon.trace.Tracer
import model.FileIO._
import model._
import org.joda.time.{DateTime, DateTimeZone}

import scala.collection.JavaConverters._

class CassandraRepositoryJavaDriver {

  private def profile[R](code: => R, t: Long = nanoTime): (R, Long) = (code, nanoTime - t)

  val nrOfCacheEntries: Int = 100
  val poolingOptions = new PoolingOptions
  val cluster: Cluster = Cluster.builder ()
                         .withAuthProvider ( new DsePlainTextAuthProvider ( cassandraLogin, cassandraPassword ) )
                         .addContactPoints ( cassandraAddress: _* )
                         .withPort ( cassandraPort )
                         .withPoolingOptions ( poolingOptions )
                         .build ()
  val session: Session = cluster.newSession ()
  val cache: LoadingCache[String, PreparedStatement] =
    CacheBuilder.newBuilder ().
    maximumSize ( nrOfCacheEntries ).
    build (
      new CacheLoader[String, PreparedStatement]() {
        def load(key: String): PreparedStatement = session.prepare ( key.toString )
      }
    )


  def executeQuery(queryText: String): Unit = try {
    println ( s"Execute Query: $queryText" )
    val rs = session.execute ( queryText.stripMargin ).iterator ()
    rs.forEachRemaining ( row => println (
      row.getColumnDefinitions.asList ().toArray.toList.map ( col => s"$col: ${row.getString ( col.toString )}" ).mkString ( "," )
    ) )
  } catch {
    case ex: Exception =>
      println ( s"Exception: ${ex.getMessage}" )
  }


  object CassandraObject {

    def toDateTime(row: Row, col: String): Option[DateTime] =
      if (row.getObject ( col ) == null) None
      else Some ( new DateTime ( row.getTimestamp ( col ).getTime, DateTimeZone.getDefault ) )

    def toInt(row: Row, col: String): Option[Int] =
      if (row.getObject ( col ) == null) None
      else Some ( row.getInt ( col ) )

    def toUUID(row: Row, col: String): Option[UUID] =
      if (row.getObject ( col ) == null) None
      else Some ( row.getUUID ( col ) )

    def toString(row: Row, col: String): Option[String] =
      if (row.getObject ( col ) == null) None
      else Some ( row.getString ( col ) )

    def toBigDecimal(row: Row, col: String): Option[BigDecimal] =
      if (row.getObject ( col ) == null) None
      else Some ( row.getDecimal ( col ) )

    def toBoolean(row: Row, col: String): Option[Boolean] =
      if (row.getObject ( col ) == null) None
      else Some ( row.getBool ( col ) )


    def getFileParams(cache: LoadingCache[String, PreparedStatement], session: Session)
                     (tableName: String, queryBy: String, queryByValue: String): Seq[FileParams] = {
      val query: Statement =
        QueryBuilder.select ().
        all ().
        from ( cassandraKeyspace, tableName ).
        where ( QueryBuilder.eq ( queryBy, QueryBuilder.bindMarker () ) )

      session.execute ( cache.get ( query.toString ).bind ( queryByValue ) ).all ().asScala.map (
        row => FileParams (
          secret = row.getString ( "secret" ),
          projectId = row.getString ( "project" ),
          name = row.getString ( "name" ),
          size = row.getLong ( "size" ),
          contentType = row.getString ( "content_type" ) )
      )
    }

    val getFileParams: (String, String, String) => Seq[FileParams] = getFileParams ( cache, session )

    def getNextId(cache: LoadingCache[String, PreparedStatement], session: Session)(id: String): Option[Long] = {
      val query: Statement =
        QueryBuilder.select ().
        all ().
        from ( cassandraKeyspace, "ids" ).
        where ( QueryBuilder.eq ( "id", QueryBuilder.bindMarker () ) )

      session.execute ( cache.get ( query.toString ).bind ( id ) ).all ().asScala.map ( _.getLong ( "next_id" ) ).headOption
    }

    val getNextId: String => Option[Long] = getNextId ( cache, session )




    def getDbUser(cache: LoadingCache[String, PreparedStatement], session: Session)(login: String): Option[DbUser] = {
      val query: Statement =
        QueryBuilder.select ().
        all ().
        from ( cassandraKeyspace, DbUser.tableName ).
        where ( QueryBuilder.eq ( "login", QueryBuilder.bindMarker () ) )

      session.execute ( cache.get ( query.toString ).bind ( login ) ).all ().asScala.map ( row => DbUser (
        login = row.getString ( "login" ),
        password = row.getString ( "password" ),
        permissions = row.getString ( "permissions" ),
        token = row.getUUID ( "token_id" ),
        tokenCreatedOn = toDateTime ( row, "token_created_on" ).get ) )
      .headOption
    }

    val getDbUser: String => Option[DbUser] = getDbUser ( cache, session )


    def getDbUserToken(cache: LoadingCache[String, PreparedStatement], session: Session)(token: UUID): Option[DbUserToken] = {
      val query: Statement =
        QueryBuilder.select ().
        all ().
        from ( cassandraKeyspace, DbUserToken.tableName ).
        where ( QueryBuilder.eq ( "token_id", QueryBuilder.bindMarker () ) )

      session.execute ( cache.get ( query.toString ).bind ( token ) ).all ().asScala.map ( row => DbUserToken (
        token = row.getUUID ( "token_id" ),
        login = row.getString ( "login" ),
        permissions = row.getString ( "permissions" ),
        tokenCreatedOn = toDateTime ( row, "token_created_on" ).get ) )
      .headOption
    }

    val getDbUserToken: UUID => Option[DbUserToken] = getDbUserToken ( cache, session )


  }



  def setFileParams(fileParams: FileParams): Unit =
    Tracer.withNewContext ( "CassandraRepository___setFileParams", autoFinish = true ) {
      session.execute ( QueryBuilder.insertInto (
        cassandraKeyspace, FileParamsBySecret.tableName ).values ( FileParamsBySecret.cols, fileParams.toBySecret.values ) )
      session.execute ( QueryBuilder.insertInto (
        cassandraKeyspace, FileParamsByProject.tableName ).values ( FileParamsByProject.cols, fileParams.toByProject.values ) )
      session.execute ( QueryBuilder.insertInto (
        cassandraKeyspace, FileParamsByName.tableName ).values ( FileParamsByName.cols, fileParams.toByName.values ) )
    }

  def getFileParams(fileParamsQuery: FileParamsQuery): Seq[FileParams] =
    Tracer.withNewContext ( "FileParamsQuery___getFileName", autoFinish = true ) {
      fileParamsQuery.secret match {
        case Some ( secret ) => CassandraObject.getFileParams ( FileParamsBySecret.tableName, FileParamsBySecret.col, secret )
        case _ => fileParamsQuery.projectId match {
          case Some ( projectId ) => CassandraObject.getFileParams ( FileParamsByProject.tableName, FileParamsByProject.col, projectId )
          case _ => fileParamsQuery.name match {
            case Some ( name ) => CassandraObject.getFileParams ( FileParamsByName.tableName, FileParamsByName.col, name )
            case _ => Seq.empty
          }
        }
      }
    }


  def getNextId(id: String): Long =
    Tracer.withNewContext ( "FileParamsQuery___getNextId", autoFinish = true ) {
      session.execute ( s"UPDATE $cassandraKeyspace.ids SET next_id = next_id + 1 WHERE id = '$id';" )
      CassandraObject.getNextId ( id ).getOrElse ( 0 )
    }


  def getDbUserAndUpdateToken(login: String, password: String): Option[DbUser] =
    Tracer.withNewContext ( "CassandraRepositoryJavaDriver___getDbUserAndUpdateToken", autoFinish = true ) {
      val dbUserOptional: Option[DbUser] = CassandraObject.getDbUser ( login )
      dbUserOptional match {
        case Some ( dbUser ) =>
          if (dbUser.password == password) {
            if (dbUser.tokenCreatedOn.plusDays ( 1 ).getMillis < DateTime.now.getMillis) {
              println ( "Regenerate token" )
              session.execute ( s"DELETE FROM $cassandraKeyspace.db_user_token WHERE token_id = ${dbUser.token}" )
              val dbUserUpdated = dbUser.copy ( token = UUID.randomUUID, tokenCreatedOn = DateTime.now )
              setDbUser ( dbUserUpdated )
              setDbUserToken ( DbUserToken ( dbUserUpdated.token, dbUserUpdated.login, dbUserUpdated.permissions, dbUserUpdated.tokenCreatedOn ) )
              CassandraObject.getDbUser ( login )
            } else {
              dbUserOptional
            }
          }
          else dbUserOptional
        case None => dbUserOptional
      }
    }

  def getDbUser(login: String): Option[DbUser] =
    Tracer.withNewContext ( "CassandraRepositoryJavaDriver___getDbUser", autoFinish = true ) {
      CassandraObject.getDbUser ( login )
    }

  def setDbUser(dbUser: DbUser): Unit =
    Tracer.withNewContext ( "CassandraRepositoryJavaDriver___setDbUser", autoFinish = true ) {
      session.execute (
        QueryBuilder.insertInto ( cassandraKeyspace, DbUser.tableName ).values ( DbUser.cols, dbUser.values ) )
    }

  def getDbUserToken(token: UUID): Option[DbUserToken] =
    Tracer.withNewContext ( "CassandraRepositoryJavaDriver___getDbUserToken", autoFinish = true ) {
      CassandraObject.getDbUserToken ( token )
    }

  def setDbUserToken(dbUserToken: DbUserToken): Unit =
    Tracer.withNewContext ( "CassandraRepositoryJavaDriver___setDbUserToken", autoFinish = true ) {
      session.execute (
        QueryBuilder.insertInto ( cassandraKeyspace, DbUserToken.tableName ).values ( DbUserToken.cols, dbUserToken.values ) )
    }



}
