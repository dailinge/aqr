package aqr

/**
 * Simulates the workflow of generating fill and price update messages
 * and sending them to the Position Service.
 */
object Simulator extends App {
  case class Config(fillsFile: String = "", pricesFile: String = "")

  val parser = new scopt.OptionParser[Config]("scopt") {
    head("trades simulator")

    opt[String]("fills").required().valueName("<filePath>")
      .action { (f, c) => c.copy(fillsFile = f) }
      .text("a file specifying fills is required")

    opt[String]("prices").required().valueName("<filePath>")
      .action { (p, c) => c.copy(pricesFile = p) }
      .text("a file specifying price updates is required")
  }

  val config = parser.parse(args, Config()) match {
    case Some(config) => config
    case None => throw new RuntimeException("one or more arguments are missing")
  }

  val messageReplay = new MessageReplay(config.fillsFile, config.pricesFile)

  PositionService.init()

  while(messageReplay.hasNext) {
    PositionService.receiveMessage(messageReplay.next)
  }
}
