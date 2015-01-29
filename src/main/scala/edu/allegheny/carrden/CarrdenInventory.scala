package edu.allegheny.carrden

import java.sql.Date

import Tables._
import org.h2.jdbc.JdbcSQLException
import org.scalatra._
import org.scalatra.json._
import org.json4s.{DefaultFormats, Formats}
import scalate.ScalateSupport
import scala.slick.driver.H2Driver.simple._
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession
import scala.util.{Try, Success, Failure}

case class CarrdenInventory(db: Database) extends CarrdenInventoryStack with JacksonJsonSupport {

  protected implicit val jsonFormats: Formats = DefaultFormats

  case class Sale(sold: Map[String, String])

  case class SaleResult(price: Double)

  get("/") {
    contentType = "text/html"
    jade(
      "main",
      "inventory" -> db.withDynSession {
        produce.list.map {
          case (name, num, price) =>
            s"We have $num $name and they cost $price dollars"
        }
      },
      "produceKinds" -> db.withDynSession {
        produce.map(_.name).list
      }
    )
  }

  get("/produce") {
    db withDynSession {
      contentType = "text/html"
      produce.list.map {
        case (name, num, price) =>
          s"We have $num $name and they cost $price dollars"
      } mkString "<br />"
    }
  }

  post("/sale/") {
    contentType = formats("json")
    log(s"Recieved $params")
    try {
      val date = new Date(System.currentTimeMillis())
      val sold = params.mapValues{case "" => 0; case s: String => Integer.parseInt(s)}
      log(s"Extracted: $sold")
      db withDynTransaction {
        // start a new transaction
        // (if 2+ point-of-sale clients are connected, we don't want
        // them to try to add sales with the same number)
        val saleNum = sales // the sale number is equal to...
          .filter(_.date === date) // get all sale records for today
          .map(_.saleNum) // extract the sale numbers
          .list
          .reduceOption(_ max _)
          .getOrElse(0) + 1 // find the last sale number & increment
        val processed = for {(item, count) <- sold if count > 0}
          yield {
            // generate the tuples to insert into the DB
            (date,
              saleNum,
              item,
              count,
              produce // get the current price for that produce item
                .filter(_.name === item) // select the row for this item
                .map(_.price) // extract the price
                .first * count // multiply by the amount sold
              )
          }
        sales ++= processed // insert rows into the DB
        Ok(SaleResult(processed.map(_._5).sum)) // reply to the client w/ the cost (wrapped in a 200 OK)
      }
    } catch {
      //TODO: better error handling pls
      case e: Exception => log("failed: ", e); InternalServerError(e.toString)
    }
  }
}
case class CarrdenAdmin(db: Database) extends CarrdenInventoryStack {

  get("/") {
    contentType="text/html"
    jade(
      "admin",
      "inventory" -> (Try(db.withDynSession {
        produce.list.map { case (name, num, price) => name -> num }.toMap
      }) match {
        case Success(fully: Map[String,Any]) => fully;
        case Failure(JdbcSQLException) => Map[String,Any]()
      }),
      "sales" -> (Try(db.withDynSession {
        sales.list
      }) match {
        case Success(fully: List[(Date,Int,String,Int,Double)]) => fully;
        case Failure(JdbcSQLException) => List[(Date,Int,String,Int,Double)]()
      })
    )
  }

  get("/db/create-tables/") {
    Try(
      db withDynSession ( produce.ddl ++ sales.ddl).create
    ) match {
      case Success(_) => Created("tables created successfully")
      // TODO: match possible reasons tables could not be created
      case Failure(why) => InternalServerError(why.toString)
    }
  }

  post("/db/drop-tables/") {
    Try(
      db withDynSession ( produce.ddl ++ sales.ddl ).drop
    ) match {
      case Success(_) => Ok("tables dropped successfully")
      // TODO: match possible reasons tables could not be dropped
      case Failure(why) => InternalServerError(why.toString)
    }
  }

  get("/db/load-test-data/") {
    db withDynSession {
      produce insertAll (
        ("To-may-to", 30, 1.25),
        ("To-mah-to", 30, 1.25),
        ("Po-tay-to", 42, 3.50),
        ("Po-tah-to", 42, 3.50),
        ("A vegetable with a space", 10000, 0.00),
        // PATHOLOGICAL VEGETABLE PLS IGNORE
          //("324q54w56e7890*^&&*^%^&%%^$$%#$##$@#$@@#$$@#$#@$%#^%&^*&^*", -99999, -203.9),
        // INCREASINGLY PATHOLOGICAL VEGETABLE PLS IGNORE
        ("zalgoberry", Integer.MIN_VALUE, Double.NaN)
      )
    }
  }

}
