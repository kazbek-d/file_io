package graphql

import java.util.UUID

import org.joda.time.DateTime
import sangria.schema.{ScalarAlias, StringType}
import sangria.validation.ValueCoercionViolation
import sangria.schema._

object CommonQL {


  case object IDViolation extends ValueCoercionViolation("Invalid ID")
  val UUIDType = ScalarAlias[UUID, String](IDType,
    toScalar = _.toString,
    fromScalar = idString ⇒ try Right(UUID.fromString(idString)) catch {
      case _: IllegalArgumentException ⇒ Left(IDViolation)
    })


  case object DateTimeViolation extends ValueCoercionViolation("Invalid DateTime")
  val DateTimeType = ScalarAlias[DateTime, String](StringType,
    toScalar = _.toString,
    fromScalar = dataTimeString ⇒ try Right(DateTime.parse(dataTimeString)) catch {
      case _: IllegalArgumentException ⇒ Left(DateTimeViolation)
    })


}
