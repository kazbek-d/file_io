package common

import java.text.SimpleDateFormat
import java.time.ZonedDateTime

import org.joda.time.{DateTime, DateTimeZone}
import org.joda.time.format.ISODateTimeFormat
import spray.json._

object DateTimeUtils {
  def toPk(yyyy: Int, mm: Int, day: Int, hour: Int) =
    s"$yyyy-$mm-$day-$hour"


  implicit class ExtentionDateTime(val dt: DateTime) {
    def toZonedDateTime = dt.toGregorianCalendar.toZonedDateTime
  }

  implicit class ExtentionLong(val utc: Long) {
    def toZonedDateTime = new DateTime(utc, DateTimeZone.UTC).toZonedDateTime
  }

  def toDateTime(someString: String)(implicit sdf: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")) =
    if (someString == "null") None
    else {
      try {
        val date = sdf.parse(someString.replace('T', ' '))
        Some(new DateTime(date))
      } catch {
        case e: Exception => None
      }
    }

  implicit class ExtentionZonedDateTime(val zdt: ZonedDateTime) {
    def toDateTime = {
      val zone = DateTimeZone.forID(zdt.getZone.getId)
      new DateTime(zdt.toInstant.toEpochMilli, zone)
    }

    def getMillis = {
      zdt.toDateTime.getMillis
    }
  }

  implicit def dateTimeOrdering: Ordering[DateTime] = Ordering.fromLessThan(_ isBefore _)


  implicit object DateTimeFormat extends RootJsonFormat[DateTime] {

    val formatter = ISODateTimeFormat.basicDateTimeNoMillis

    def write(obj: DateTime): JsValue = {
      JsString(formatter.print(obj))
    }

    def read(json: JsValue): DateTime = json match {
      case JsString(s) => try {
        formatter.parseDateTime(s)
      }
      catch {
        case t: Throwable => error(s)
      }
      case _ =>
        error(json.toString())
    }

    def error(v: Any): DateTime = {
      val example = formatter.print(0)
      deserializationError(f"'$v' is not a valid date value. Dates must be in compact ISO-8601 format, e.g. '$example'")
    }
  }

}
