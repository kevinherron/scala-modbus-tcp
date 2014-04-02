package com.digitalpetri.modbus.examples.master

import com.digitalpetri.modbus.master.ModbusTcpMaster
import com.digitalpetri.modbus.master.ModbusTcpMaster.ModbusTcpMasterConfig
import com.digitalpetri.modbus.{Modbus, ReadHoldingRegistersResponse, ReadHoldingRegistersRequest}
import com.typesafe.scalalogging.slf4j.Logging
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object MasterExample extends App with Logging {

  implicit val ec = ExecutionContext.global

  val config = new ModbusTcpMasterConfig(host = "localhost", port = 50200)
  val master = new ModbusTcpMaster(config)

  val responseFuture = master.sendRequest(ReadHoldingRegistersRequest(0, 10))

  responseFuture.onComplete {
    case Success(response) =>
      response match {
        case ReadHoldingRegistersResponse(_) => logger.info(s"Received response: $response")
        case _ => logger.error(s"Unexpected response: $response")
      }

    case Failure(ex) => logger.error(s"Error reading holding registers: $ex")
  }

  responseFuture.onComplete {
    case _ =>
      master.disconnect()
      Modbus.SharedEventLoop.shutdownGracefully()
      Modbus.SharedWheelTimer.stop()
  }

}
