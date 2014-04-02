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

package com.digitalpetri.modbus.master

import com.digitalpetri.modbus.ModbusResponse
import com.digitalpetri.modbus.layers.TcpPayload
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import org.slf4j.LoggerFactory
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}

class ModbusTcpResponseDispatcher(responseHandler: TcpServiceResponseHandler,
                                  executionContext: ExecutionContext) extends SimpleChannelInboundHandler[TcpPayload] {

  val logger = LoggerFactory.getLogger(getClass)

  def channelRead0(ctx: ChannelHandlerContext, payload: TcpPayload): Unit = {
    val funcTry = for {
      response <- Try(payload.pdu.asInstanceOf[ModbusResponse])
    } yield {
      new TcpServiceResponse(payload.transactionId, payload.unitId, response)
    }

    funcTry match {
      case Success(response) => dispatch(response)
      case Failure(ex) => logger.error(s"pdu was not instance of ModbusResponse: ${payload.pdu}")
    }
  }

  private def dispatch(response: TcpServiceResponse) {
    executionContext.execute(new Runnable {
      override def run(): Unit =
        try {
          responseHandler.onServiceResponse(response)
        } catch {
          case t: Throwable => logger.error(s"Error delivering service response.", t)
        }
    })
  }

}
