package edu.allegheny.carrden

import org.scalatra._
import scalate.ScalateSupport

class CarrdenInventory extends CarrdenInventoryStack {

  get("/") {
    <html>
      <body>
        <h1>Hello, world!</h1>
        Say <a href="hello-scalate">hello to Scalate</a>.
      </body>
    </html>
  }
  
}
