package graphql

import sangria.schema._


object UserModel {

  case class QlUser(userName: String, permissions: List[String])


  val UserNameArg = Argument("userName", StringType)
  val PasswordArg = Argument("password", StringType)

  val UserType = ObjectType("User", fields[SecureContext, QlUser](
    Field("userName", StringType, resolve = _.value.userName),
    Field("permissions", OptionType(ListType(StringType)),
      resolve = ctx ⇒ ctx.ctx.authorised("VIEW_PERMISSIONS") { _ ⇒
        ctx.value.permissions
      })
  ))



}
