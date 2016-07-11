package aqr

import scala.collection.mutable
import scala.util.{Failure, Try}

object PositionService {
  import Event._

  case class PLRecord(price: Float, shares: Int)

  val DefaultHolding = PLRecord(0.0.toFloat, 0)
  val kvStore: mutable.HashMap[String, PLRecord] = mutable.HashMap.empty[String, PLRecord]
  var cash: Float = 0.0.toFloat

  def init(): Unit = {
    kvStore.clear()
    cash = 0.0.toFloat
  }

  def receiveMessage(message: String): Unit = {
    Try(parseMessage(message)) flatMap { event =>
      Try(processEvent(event))
    } match {
      case Failure(e) => println(e)
      case _ =>
    }
  }

  private[this] def totalValue(): Float =
    (kvStore.map { case (k, h) => h.price * h.shares.toFloat }).sum + cash

  private[this] def formatPNLMessage(
    timestamp: String,
    symbol: String,
    shares: Int,
    total: Float
  ) = {
    f"PNL $timestamp%s $symbol%s $shares%d $total%2.2f"
  }

  private[this] def processEvent(event: Event) = event match {
    case Fill(symbol, price, shares, action) => {
      val sharesChange = action match {
        case "B" => shares
        case "S" => -1 * shares
      }

      val currentHolding = kvStore.getOrElse(symbol, DefaultHolding)
      kvStore.update(
        symbol,
        currentHolding.copy(shares = currentHolding.shares + sharesChange)
      )
      cash -= sharesChange * price
    }
    case PriceUpdate(timestamp, symbol, price) => {
      val currentHolding = kvStore.getOrElse(symbol, DefaultHolding)
      val currentShares = currentHolding.shares
      kvStore.update(
        symbol,
        currentHolding.copy(price = price)
      )
      println(
        formatPNLMessage(timestamp, symbol, currentShares, totalValue)
      )
    }
  }
}
