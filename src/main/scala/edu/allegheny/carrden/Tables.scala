package edu.allegheny.carrden

import java.sql.Date

import scala.slick.driver.H2Driver.simple._
/**
 * Created by hawk on 1/27/15.
 */
object Tables {

  class Produce(tag: Tag) extends Table[(String,Int,Double)](tag,"PRODUCE") {
    def name  = column[String]("NAME", O.PrimaryKey)
    def count = column[Int]("COUNT", O.NotNull)
    def price = column[Double]("PRICE", O.NotNull)

    // Every table needs a * projection with the same type as the table's type parameter
    def * = (name,count,price)
  }

  val produce = TableQuery[Produce]

  class Sales(tag: Tag) extends Table[(Date, Int,String,Int,Double)](tag,"SALES") {
    def date    = column[Date]("DATE", O.NotNull)
    def saleNum = column[Int]("SALE_NUM", O.NotNull)
    def item    = column[String]("ITEM", O.NotNull)
    def count   = column[Int]("COUNT", O.NotNull)
    def revenue = column[Double]("REVENUE", O.NotNull)

    def itemFK = foreignKey("ITEM_FK", item, produce )(
        _.name,
        onUpdate=ForeignKeyAction.Restrict,
        onDelete=ForeignKeyAction.NoAction
      )
    def salePK = primaryKey("salePK", (date,saleNum,item))

    // Every table needs a * projection with the same type as the table's type parameter
    def * = (date,saleNum,item,count,revenue)
  }

  val sales = TableQuery[Sales]

}
