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
