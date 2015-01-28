package edu.allegheny.carrden

import scala.slick.driver.H2Driver.simple._
/**
 * Created by hawk on 1/27/15.
 */
object Tables {

  class Produce(tag: Tag) extends Table[(String,Int,Double)](tag,"PRODUCE") {
    def name  = column[String]("NAME", O.PrimaryKey)
    def count = column[Int]("COUNT")
    def price = column[Double]("PRICE")

    // Every table needs a * projection with the same type as the table's type parameter
    def * = (name,count,price)
  }

  val produce = TableQuery[Produce]

}
