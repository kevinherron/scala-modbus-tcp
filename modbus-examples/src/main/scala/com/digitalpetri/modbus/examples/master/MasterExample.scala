/*
 * Copyright 2014 Kevin Herron
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.digitalpetri.modbus.examples.master

import com.digitalpetri.modbus.master.{ModbusTcpMasterConfig, ModbusTcpMaster}
import com.digitalpetri.modbus.{Modbus, ReadHoldingRegistersResponse, ReadHoldingRegistersRequest}
import com.typesafe.scalalogging.slf4j.StrictLogging
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object MasterExample extends App with StrictLogging {

  implicit val ec = ExecutionContext.global

  val config = new ModbusTcpMasterConfig(host = "localhost", port = 50200)
  val master = new ModbusTcpMaster(config)

  val request = ReadHoldingRegistersRequest(startAddress = 0, quantity = 10)
  val response = master.sendRequest[ReadHoldingRegistersResponse](request)

  response.onComplete {
    case Success(r) => logger.info(s"Received response: $r")
    case Failure(t) => logger.error(s"Error reading holding registers: ${t.getMessage}")
  }

  response.onComplete {
    case _ =>
      master.disconnect()
      Modbus.SharedEventLoop.shutdownGracefully()
      Modbus.SharedWheelTimer.stop()
  }

}
