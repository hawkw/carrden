package me.hawkweisman.common

import java.security.{NoSuchAlgorithmException, MessageDigest}

/**
 * Created by hawk on 2/18/15.
 */
package object authtools {

  /**
   *
   * @param pass The password to hash.
   * @param algorithm The hashing algorithm. Defaults to SHA-512.
   * @param saltLength The length of the salt string. Default is 16.
   * @param random An instance of [[scala.util.Random]] for generating the salt
   * @throws NoSuchAlgorithmException if the specified hashing algorithm doesn't exist
   * @return A tuple containing (hash, salt) as strings
   */
  @throws[NoSuchAlgorithmException]("if the specified hashing algorithm doesn't exist")
  def hash
  (pass: String, algorithm: String = "SHA_512", saltLength: Int = 16)
  (implicit random: scala.util.Random): (String,String) = {
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
