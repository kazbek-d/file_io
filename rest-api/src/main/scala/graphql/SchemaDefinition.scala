package graphql

import sangria.schema._


object SchemaDefinition {

  val leomaxSchema = Schema (
    query = SchemaDefinitionQueries.queries,
    mutation = Some ( SchemaDefinitionMutations.mutations )
  )

}





