
package jforth.scalacode

import java.math.BigInteger

import scala.annotation.tailrec
import scala.collection.JavaConverters.seqAsJavaList
import scala.math.BigInt


object MyMath
{
  def factorial(n: Long): BigInt =
  {
    def fac(n: BigInt, f: BigInt): BigInt =
      if (n == 0) f else fac(n-1, n*f)
    fac(n, 1)
  }

  def bigPow (a: Long,  b: Int): BigInt =
  {
    BigInt(a).pow(b)
  }

  def fibonacci (x: Long): BigInt =
  {
    @tailrec def fibHelper(x: Long, prev: BigInt = 0, next: BigInt = 1): BigInt = x match
    {
      case 0 => prev
      case 1 => next
      case _ => fibHelper(x - 1, next, next + prev)
    }
    fibHelper(x)
  }

  def primeFactors(n: BigInt): List[BigInt] =
  {
    def loop(nn: BigInt, m: BigInt, as: List[BigInt]): List[BigInt] =
      if (m > nn) as
      else if (nn % m == 0) loop(nn / m, m, m :: as)
      else loop(nn, m + 1, as)

    loop(n, 2, Nil)
  }

  def toJList (l: List[BigInt]): java.util.List[BigInt] = seqAsJavaList(l)

  def fromBinaryListBig(in: Array[Int]): BigInt = {
    var sum: BigInt = 0
    in foreach (x => {sum <<= 1; sum += (x&1)})
    sum
  }

  def fromBinaryListLong(in: Array[Int]): Long = {
    var sum: Long = 0
    in foreach (x => {sum <<= 1; sum += (x&1)})
    sum
  }

}