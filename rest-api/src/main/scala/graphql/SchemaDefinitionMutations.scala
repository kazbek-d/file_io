package graphql

import sangria.schema._

object SchemaDefinitionMutations {

  val mutations = ObjectType (
    "Mutation",
    "Login and other mutations",
    fields [SecureContext, Unit](
      Field ( "login", OptionType ( StringType ),
        arguments = UserModel.UserNameArg :: UserModel.PasswordArg :: Nil,
        resolve = ctx ⇒ UpdateCtx ( ctx.ctx.login ( ctx.arg ( UserModel.UserNameArg ), ctx.arg ( UserModel.PasswordArg ) ) ) { token ⇒
          ctx.ctx.copy ( token = Some ( token ) )
        } ),


    ) )

}
