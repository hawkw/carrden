package edu.allegheny.carrden

import Tables._
import org.scalatra._
import scalate.ScalateSupport
import scala.slick.driver.H2Driver.simple._
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession

case class CarrdenInventory(db: Database) extends CarrdenInventoryStack {

  get("/") {
    contentType="text/html"
    contentType="text/html"
    jade(
        "main",
        "inventory" -> db.withDynSession {
          produce.list.map { case (name, num, price) => s"We have $num $name and they cost $price dollars"}
        },
        "produceKinds" -> db.withDynSession { produce.map(_.name).list }
    )
  }

  get("/produce") {
    db withDynSession {
      contentType = "text/html"
      produce.list.map { case (name, num, price) => s"We have $num $name and they cost $price dollars"} mkString "<br />"
    }
  }
  
}
case class CarrdenAdmin(db: Database) extends CarrdenInventoryStack {

  get("/") {
    contentType="text/html"
    jade("admin", "inventory" -> db.withDynSession {
      produce.list.map { case (name, num, price) => s"We have $num $name and they cost $price dollars"}
    })
  }

  get("/db/create-tables/") {
    db withDynSession { produce.ddl.create }
  }

  get("/db/load-test-data/") {
    db withDynSession {
      produce insertAll (
        ("To-may-to", 30, 1.25),
        ("To-mah-to", 30, 1.25),
        ("Po-tay-to", 42, 3.50),
        ("Po-tah-to", 42, 3.50)
      )
    }
  }

}
