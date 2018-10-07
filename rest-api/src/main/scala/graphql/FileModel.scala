package graphql

import common.Settings.fileIOprefix
import model.FileIO.FileParams
import model.RestApi.FileParamsResponse
import sangria.marshalling.{CoercedScalaResultMarshaller, FromInput}
import sangria.schema._

object FileModel {

  val FileParamsInputType = InputObjectType[FileParams]("FileParamsInputType", List(
    InputField("secret", StringType, "secret / file's Id"),
    InputField("projectId", StringType, "projectId"),
    InputField("name", StringType, "File's name"),
    InputField("size", LongType, "File's size"),
    InputField("contentType", StringType, "File's Content-Type")
  ))
  val FileParamsType: ObjectType[Repo, FileParams] =
    ObjectType (
      "GetFileNameGraphQL",
      "File's metadata",
      fields [Repo, FileParams](
        Field ( "url", StringType, Some ( "File's URL (Content-Type: application/octet-stream)" ),
          resolve = c => s"$fileIOprefix/file/${c.value.projectId}/${c.value.secret}" ),
        Field ( "urlProtect", StringType, Some ( "File's URL (Content-Type: depends on file's extension)" ),
          resolve = c => s"$fileIOprefix/protectfile/${c.value.projectId}/${c.value.secret}" ),
        Field ( "secret", StringType, Some ( "secret / file's Id" ), resolve = _.value.secret ),
        Field ( "projectId", StringType, Some ( "projectId" ), resolve = _.value.projectId ),
        Field ( "name", StringType, Some ( "File's name" ), resolve = _.value.name ),
        Field ( "size", LongType, Some ( "File's size" ), resolve = _.value.size ),
        Field ( "contentType", StringType, Some ( "File's Content-Type" ), resolve = _.value.contentType )
      ) )
  implicit val FileParamsInputTypeManual = new FromInput[FileParams] {
    val marshaller = CoercedScalaResultMarshaller.default

    def fromResult(node: marshaller.Node) = {
      val ad = node.asInstanceOf[Map[String, Any]]
      FileParams(
        secret = ad.get("secret").asInstanceOf[String],
        projectId = ad.get("projectId").asInstanceOf[String],
        name = ad.get("name").asInstanceOf[String],
        size = ad.get("size").asInstanceOf[Long],
        contentType = ad.get("contentType").asInstanceOf[String]
      )
    }
  }

  val fileParamsArg = Argument("fileParams", OptionInputType(FileParamsInputType), "File")

  val FileParamsListType: ObjectType[Repo, FileParamsResponse] =
    ObjectType (
      "GetFileParamsResponse",
      "Collection of files' metadata",
      fields [Repo, FileParamsResponse](
        Field ( "fileParams", ListType ( FileParamsType ), Some ( "Collection of files' metadata" ), resolve = _.value.fileParamsSeq )
      ) )

  val queryBySecret = Argument ( "secret", StringType, description = "secret / file-s Id" )
  val queryByProject = Argument ( "projectId", StringType, description = "projectId" )
  val queryByName = Argument ( "name", StringType, description = "File's name" )

}
