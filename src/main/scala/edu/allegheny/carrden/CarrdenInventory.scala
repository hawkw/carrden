package edu.allegheny.carrden

import org.scalatra._
import scalate.ScalateSupport
import scala.slick.driver.H2Driver.simple._
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession

case class CarrdenInventory(db: Database) extends CarrdenInventoryStack {

  get("/") {
    contentType="text/html"
    jade("hello-scalate")
  }
  
}
case class CarrdenAdmin(db: Database) extends CarrdenInventoryStack {

  get("/") {
    contentType="text/html"
    jade("admin")
  }

}
