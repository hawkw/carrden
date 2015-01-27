package edu.allegheny.carrden

import org.scalatra._
import scalate.ScalateSupport
import scala.slick.driver.H2Driver.simple._
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession

case class CarrdenInventory(db: Database) extends CarrdenInventoryStack {

  get("/") {
    <html>
      <body>
        <h1>Hello, world!</h1>
        Say <a href="hello-scalate">hello to Scalate</a>.
      </body>
    </html>
  }
  
}
