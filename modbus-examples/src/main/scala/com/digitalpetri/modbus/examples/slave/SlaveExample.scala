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

package com.digitalpetri.modbus.examples.slave

import com.digitalpetri.modbus.Modbus
import com.digitalpetri.modbus.slave.{ModbusTcpSlaveConfig, ModbusTcpSlave}
import com.typesafe.scalalogging.slf4j.Logging
import scala.concurrent.ExecutionContext
import scala.util.Failure
import scala.util.Success

object SlaveExample extends App with Logging {

  implicit val ec = ExecutionContext.global

  val config  = new ModbusTcpSlaveConfig()
  val slave   = new ModbusTcpSlave(config)

  slave.setRequestHandler(new ReadWriteRequestHandler)

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
