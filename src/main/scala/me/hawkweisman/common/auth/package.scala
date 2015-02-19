package me.hawkweisman.common

/**
 * Created by hawk on 2/18/15.
 */
package object auth {
  def randomString(alphabet: String)(n: Int)(random: scala.util.Random): String =
    Stream.continually(random.nextInt(alphabet.size))
      .map(alphabet)
      .take(n)
      .mkString

  def randomAlphanumericString(n: Int)(random: scala.util.Random) =
    randomString("abcdefghijklmnopqrstuvwxyz0123456789")(n)(random)

}
