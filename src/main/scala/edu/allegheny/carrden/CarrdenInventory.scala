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
import scala.slick.jdbc.{GetResult, StaticQuery => Q}

case class CarrdenInventory(db: Database) extends CarrdenInventoryStack with JacksonJsonSupport {

  protected implicit val jsonFormats: Formats = DefaultFormats

  case class Sale(sold: Map[String, String])

  case class SaleResult(price: Double)
  case class OutOfStock(what: String)

  case class OutOfStockException(what: String) extends Exception

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
      val date = new Date(System.currentTimeMillis())
      val sold = params.mapValues{case "" => 0; case s: String => Integer.parseInt(s)}
      log(s"Extracted: $sold")
      db withDynTransaction {
        Try {
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
          for {sale <- processed} {
            val (item, soldCount: Int) = (sale._3, sale._4)
            val currentCount: Int = produce // get the current count of that item
              .filter(_.name === item)
              .map(_.count)
              .list
              .head
            val amount = currentCount - soldCount
            if (amount <= 0){
              throw OutOfStockException(item)
            } else {
              val update = for {
                inventory <- produce if inventory.name === item
              } yield inventory.count
              // subtract the amount sold from the previous amount
              update.update(amount)
            }
          }
          processed.map(_._5).sum
        } match {
          case Success(price) => Ok(SaleResult(price)) // reply to the client w/ the cost (wrapped in a 200 OK)
          case Failure(OutOfStockException(what)) => Ok(OutOfStock(what))
          case Failure(why) => InternalServerError(why.toString)
        }
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
        case Failure(a: JdbcSQLException) => Map[String,Any]()
      }),
      "sales" -> (Try(db.withDynSession {
        sales.list
      }) match {
        case Success(fully: List[(Date,Int,String,Int,Double)]) => fully;
        case Failure(a: JdbcSQLException) => List[(Date,Int,String,Int,Double)]()
      })
    )
  }

  post("/db/create-tables/") {
    log("Got request to create tables.")
    Try(
      db withDynSession {
        ( produce.schema ++ sales.schema).create


        Q.updateNA(
          "ALTER TABLE SALES" +
            "ADD CONSTRAINT CHECK(count > 0);"
        ).execute
        /*
        // this is commented out because I can't get the trigger to work.
        // TODO: I'd like the trigger to work
      Q.updateNA(
        "CREATE TRIGGER inv_update_trigger IF NOT EXISTS " +
          "AFTER INSERT ON sales FOR EACH ROW " +
          "UPDATE TABLE inventory SET count = count - new.count " +
          "WHERE name LIKE new.item;"
      ).execute
      */
      }
    ) match {
      case Success(_) => Created("tables created successfully")
      // TODO: match possible reasons tables could not be created
      case Failure(why) => InternalServerError(why.toString)
    }
  }

  post("/db/drop-tables/") {
    log("Got request to drop tables.")
    Try(
      db withDynSession ( produce.schema ++ sales.schema).drop
    ) match {
      case Success(_) => Ok("tables dropped successfully")
      // TODO: match possible reasons tables could not be dropped
      case Failure(why) => InternalServerError(why.toString)
    }
  }

  post("/db/load-test-data/") {

    log("Got request to load test db values.")
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
