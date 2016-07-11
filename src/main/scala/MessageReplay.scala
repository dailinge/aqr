package aqr

import scala.io.Source
import scala.util.{Success, Failure, Try}

object MessageReplay {
  val extractTimestamp = """[P|F] (\d+) .*""".r
}

/**
 * A wrapper that iterates through the fill and price update messages
 * provided by two separate files. This wrapper interleaves the two types
 * of messages to maintain chronological order of all the messages, assuming
 * the messages in each file are already in chronological order.
 *
 * @param fillsFile path to the file that specifies fills messages
 * @param pricesFile path to the file that specifies price update messages
 */
class MessageReplay(fillsFile: String, pricesFile: String) {
  import MessageReplay._

  val fills: BufferedIterator[String] = Source.fromFile(fillsFile).getLines.buffered
  val prices: BufferedIterator[String] = Source.fromFile(pricesFile).getLines.buffered

  def hasNext(): Boolean = fills.hasNext || prices.hasNext

  /**
   * Generates the next message. A price update message gets generated before
   * a fill one if they have the same timestamp.
   */
  def next(): String =
    (Try(fills.head), Try(prices.head)) match {
      case (Success(fill), Success(price)) =>
        (fill, price) match {
          case (extractTimestamp(fillTime), extractTimestamp(priceTime)) =>
            if (fillTime < priceTime) {
              fills.next
            } else {
              prices.next
            }
          case _ => throw new RuntimeException("invalid price or fill record")
        }
      case (_, Success(price)) => prices.next
      case (Success(fill), _) => fills.next
      case _ => throw new RuntimeException("fills and prices are both empty")
    }
}
