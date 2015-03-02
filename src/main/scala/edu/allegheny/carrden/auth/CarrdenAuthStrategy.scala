package edu.allegheny.carrden
package auth

import org.scalatra.auth.strategy.{BasicAuthStrategy, BasicAuthSupport}
import org.scalatra.auth.{ScentrySupport, ScentryConfig}
import org.scalatra.{ScalatraBase}

class CarrdenAuthStrategy(protected override val app: ScalatraBase, realm: String) extends BasicAuthStrategy[User](app, realm) {

    protected def validate(userName: String, password: String): Option[User] = {
        ??? // TODO: implement
    }

}