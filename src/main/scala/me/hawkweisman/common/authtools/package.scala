package me.hawkweisman.common

import java.security.MessageDigest

/**
 * Created by hawk on 2/18/15.
 */
package object authtools {

  def hash(pass: String, algorithm: String = "SHA_512")(implicit saltLength: Int,random: scala.util.Random): (String,String) = {
    val hasher = MessageDigest.getInstance(algorithm)
    val salt = randomAlphanumericString(saltLength)
    hasher update ( pass getBytes )
    hasher update ( salt getBytes )
    (hasher.digest.map(Integer.toHexString(_)).mkString, salt)
  }
  def randomString(alphabet: String)(n: Int)(implicit random: scala.util.Random): String =
    Stream.continually(random.nextInt(alphabet.size))
      .map(alphabet)
      .take(n)
      .mkString

  def randomAlphanumericString(n: Int)(implicit random: scala.util.Random) =
    randomString("abcdefghijklmnopqrstuvwxyz0123456789")(n)(random)

}
