package com.digitalpetri.modbus.examples.slave

import com.digitalpetri.modbus.Modbus
import com.digitalpetri.modbus.slave.ModbusTcpSlave
import com.typesafe.scalalogging.slf4j.Logging
import scala.concurrent.ExecutionContext
import scala.util.Failure
import scala.util.Success

object SlaveExample extends App with Logging {

  implicit val ec = ExecutionContext.global

  val config  = new ModbusTcpSlave.ModbusTcpSlaveConfig()
  val slave   = new ModbusTcpSlave(config)

  slave.setRequestHandler(new ReadOnlyRequestHandler)

  val host = "0.0.0.0"
  val port = 50200

  slave.bind(host, port).onComplete {
    case Success(sa) => logger.info(s"bind to $sa succeeded.")
    case Failure(ex) => logger.error(s"bind to $host:$port failed: $ex")
  }

  Runtime.getRuntime.addShutdownHook(new Thread {
    override def run(): Unit = {
      logger.info("shutting down...")
      slave.shutdown()
      Modbus.SharedEventLoop.shutdownGracefully()
      Modbus.SharedWheelTimer.stop()
    }
  })

}
