package me.hawkweisman.common

import java.security.MessageDigest

/**
 * Created by hawk on 2/18/15.
 */
package object authtools {

  def hash(pass: String)(implicit saltLength: Int,random: scala.util.Random): String = {
    val hasher = MessageDigest.getInstance("SHA_512")
    hasher update (pass getBytes)
    hasher update ( randomAlphanumericString(saltLength) getBytes )
    Seq( hasher digest ) map (Integer toHexString (_) ) mkString
  }
  def randomString(alphabet: String)(n: Int)(implicit random: scala.util.Random): String =
    Stream.continually(random.nextInt(alphabet.size))
      .map(alphabet)
      .take(n)
      .mkString

  def randomAlphanumericString(n: Int)(implicit random: scala.util.Random) =
    randomString("abcdefghijklmnopqrstuvwxyz0123456789")(n)(random)

}
