package aqr

trait Event
case class Fill(symbol: String, price: Float, shares: Int, action: String) extends Event
case class PriceUpdate(timestamp: String, symbol: String, price: Float) extends Event

object Event {
  val FillRegex = """F \d+ ([A-Z]+) ([0-9]+\.[0-9]+) ([0-9]+) ([B|S])""".r
  val PriceRegex = """P (\d+) ([A-Z]+) ([0-9]+\.[0-9]+)""".r
}
