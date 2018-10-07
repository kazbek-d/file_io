package model

import java.util.UUID

import akka.http.scaladsl.server.directives.FileInfo


object FileIO {

  def generateHash = UUID.randomUUID ().toString.filter ( x => x != '-' ).take ( 22 ).mkString

  case class FileUpload(projectId: String, fileInfo: FileInfo, file: java.io.File)

  case class FileParamsRest(secret: String, projectId: String, name: String, size: Long, contentType: String, url: String, urlProtect: String)
  case class FileParams(secret: String, projectId: String, name: String, size: Long, contentType: String) {
    override def toString() = s"secret: $secret, projectId: $projectId, name: $name, size: $size, contentType: $contentType"
    
    def toBySecret = FileParamsBySecret(secret, projectId, name, size, contentType)
    def toByProject = FileParamsByProject(secret, projectId, name, size, contentType)
    def toByName = FileParamsByName(secret, projectId, name, size, contentType)

    def toFileParamsRest = FileParamsRest(secret, projectId, name, size, contentType, "", "")
  }

  case class FileParamsQuery(secret: Option[String], projectId: Option[String], name: Option[String]) {
    override def toString() = s"secret: $secret, projectId: $projectId, name: $name"
  }


  trait FileParamsCommon {
    def toFileParams() : FileParams
  }
  // CREATE TABLE direct_tv.file_params_secret (secret varchar, project varchar, name varchar, size bigint, content_type varchar, PRIMARY KEY (secret));
  case class FileParamsBySecret(secret: String, projectId: String, name: String, size: Long, contentType: String)
    extends Cassandraable with FileParamsCommon {
    override def toString() = s"secret: $secret, projectId: $projectId, name: $name, size: $size, contentType: $contentType"

    override def toRestable: Restable = ???

    override def values: Array[AnyRef] = Array[AnyRef](secret, projectId, name, size.asInstanceOf[AnyRef], contentType)

    override def toFileParams(): FileParams = FileParams(secret, projectId, name, size, contentType)
  }
  object FileParamsBySecret {
    val tableName = "file_params_secret"
    val col = "secret"
    val cols = Array("secret", "project", "name", "size", "content_type")
  }

  // CREATE TABLE direct_tv.file_params_project (project varchar, secret varchar, name varchar, size bigint, content_type varchar, PRIMARY KEY (project, secret));
  case class FileParamsByProject(secret: String, projectId: String, name: String, size: Long, contentType: String)
    extends Cassandraable with FileParamsCommon {
    override def toString() = s"secret: $secret, projectId: $projectId, name: $name, size: $size, contentType: $contentType"

    override def toRestable: Restable = ???

    override def values: Array[AnyRef] = Array[AnyRef](projectId, secret, name, size.asInstanceOf[AnyRef], contentType)

    override def toFileParams(): FileParams = FileParams(secret, projectId, name, size, contentType)
  }
  object FileParamsByProject {
    val tableName = "file_params_project"
    val col = "project"
    val cols = Array("project", "secret", "name", "size", "content_type")
  }

  // CREATE TABLE direct_tv.file_params_name (name varchar, secret varchar, project varchar, size bigint, content_type varchar, PRIMARY KEY (name, secret));
  case class FileParamsByName(secret: String, projectId: String, name: String, size: Long, contentType: String)
    extends Cassandraable with FileParamsCommon {
    override def toString() = s"secret: $secret, projectId: $projectId, name: $name, size: $size, contentType: $contentType"

    override def toRestable: Restable = ???

    override def values: Array[AnyRef] = Array[AnyRef](name, secret, projectId, size.asInstanceOf[AnyRef], contentType)

    override def toFileParams(): FileParams = FileParams(secret, projectId, name, size, contentType)
  }
  object FileParamsByName {
    val tableName = "file_params_name"
    val col = "name"
    val cols = Array("name", "secret", "project", "size", "content_type")
  }


}
