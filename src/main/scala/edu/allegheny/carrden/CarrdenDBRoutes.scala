package edu.allegheny.carrden

import java.sql.Date
// JSON-related libraries
import org.json4s.{DefaultFormats, Formats}

// JSON handling support from Scalatra
import org.scalatra.json._

/**
 * Created by hawk on 1/28/15.
 */
trait SaleHandling extends CarrdenInventoryStack with JacksonJsonSupport {

  case class Sale(sold: Map[String,Int])

  post("sales/:date") {
    val date = new Date(System.currentTimeMillis())
    val sold = parsedBody.extract[Sale].sold
    // TODO: add sale to DB
  }
}
