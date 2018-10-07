package graphql

import sangria.schema._

object SchemaDefinitionQueries {

  val queries = ObjectType (
    "Query",
    "Login information, File.IO and etc...",
    fields [SecureContext, Unit](
      Field (
        name = "me",
        fieldType = OptionType ( UserModel.UserType ),
        resolve = ctx ⇒ ctx.ctx.authorised ()( user ⇒ user ) ),


      Field (
        name = "getFileParamsBySecret",
        description = Some ( "Поиск метаданных файла по Идентификатору Файла" ),
        fieldType = OptionType ( FileModel.FileParamsListType ),
        arguments = FileModel.queryBySecret :: Nil,
        resolve = ctx ⇒ ctx.ctx.repo.getFileParamsBySecret ( ctx arg FileModel.queryBySecret ) ),
      Field (
        name = "getFileParamsByProject",
        description = Some ( "Поиск метаданных файла по Проекту / Каталогу" ),
        fieldType = OptionType ( FileModel.FileParamsListType ),
        arguments = FileModel.queryByProject :: Nil,
        resolve = ctx ⇒ ctx.ctx.repo.getFileParamsByProject ( ctx arg FileModel.queryByProject ) ),
      Field (
        name = "getFileParamsByName",
        description = Some ( "Поиск метаданных файла по Имени Файла" ),
        fieldType = OptionType ( FileModel.FileParamsListType ),
        arguments = FileModel.queryByName :: Nil,
        resolve = ctx ⇒ ctx.ctx.repo.getFileParamsByName ( ctx arg FileModel.queryByName ) ),


    ) )

}
