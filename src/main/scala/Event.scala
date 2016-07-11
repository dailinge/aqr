package aqr

/** Represents an event processed in the Position Service */
trait Event

/**
 * Represents a Fill message
 * @param symbol symbol name of the fill
 * @param price price the fill is completed at
 * @param shares number of shares filled
 * @param action action of the fill, either a "B" (Buy) or "S" (Sell)
 */
case class Fill(symbol: String, price: Float, shares: Int, action: String) extends Event

/**
 * Represents a Price Update message
 * @param timestamp time of the price update
 * @param symbol symbol the price update is for
 * @param price new price of the symbol
 */
case class PriceUpdate(timestamp: String, symbol: String, price: Float) extends Event

/**
 * A companion object that provides the regexes and utility function to help
 * parse fill and price update messages.
  */
object Event {
  val FillRegex = """F \d+ ([A-Z]+) ([0-9]+\.[0-9]+) ([0-9]+) ([B|S])""".r
  val PriceRegex = """P (\d+) ([A-Z]+) ([0-9]+\.[0-9]+)""".r

  def parseMessage(message: String): Event = message match {
    case FillRegex(symbol, price, shares, action) =>
      Fill(symbol, price.toFloat, shares.toInt, action)
    case PriceRegex(timestamp, symbol, price) =>
      PriceUpdate(timestamp, symbol, price.toFloat)
    case _ =>
      throw new RuntimeException("invalid message format")
  }
}
