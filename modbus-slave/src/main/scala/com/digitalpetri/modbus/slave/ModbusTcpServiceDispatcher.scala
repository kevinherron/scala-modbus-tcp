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

package com.digitalpetri.modbus.slave

import com.digitalpetri.modbus._
import com.digitalpetri.modbus.layers.TcpPayload
import com.digitalpetri.modbus.slave.ServiceRequest._
import com.typesafe.scalalogging.slf4j.Logging
import io.netty.channel.{SimpleChannelInboundHandler, ChannelHandlerContext}
import scala.concurrent.ExecutionContext
import scala.util.{Success, Failure, Try}

class ModbusTcpServiceDispatcher(serviceHandler: ServiceRequestHandler,
                                 executionContext: ExecutionContext) extends SimpleChannelInboundHandler[TcpPayload] with Logging {

  def channelRead0(ctx: ChannelHandlerContext, payload: TcpPayload): Unit = {
    Try(payload.pdu.asInstanceOf[ModbusRequest]) match {
      case Success(request) =>
        dispatchRequest(request, payload, ctx)

      case Failure(ex) =>
        logger.error(s"Received unexpected payload: $payload", ex)
        ctx.disconnect()
    }
  }

  private def dispatchRequest(request: ModbusRequest, payload: TcpPayload, ctx: ChannelHandlerContext) {
    request match {
      case req: ReadHoldingRegistersRequest => dispatch {
        val s: ReadHoldingRegistersService = new TcpServiceRequest(payload.transactionId, payload.unitId, req, ctx.channel())
        serviceHandler.onReadHoldingRegisters(s)
      }

      case req: ReadInputRegistersRequest => dispatch {
        val s: ReadInputRegistersService = new TcpServiceRequest(payload.transactionId, payload.unitId, req, ctx.channel())
        serviceHandler.onReadInputRegisters(s)
      }

      case req: ReadCoilsRequest => dispatch {
        val s: ReadCoilsService = new TcpServiceRequest(payload.transactionId, payload.unitId, req, ctx.channel())
        serviceHandler.onReadCoils(s)
      }

      case req: ReadDiscreteInputsRequest => dispatch {
        val s: ReadDiscreteInputsService = new TcpServiceRequest(payload.transactionId, payload.unitId, req, ctx.channel())
        serviceHandler.onReadDiscreteInputs(s)
      }

      case req: WriteSingleCoilRequest => dispatch {
        val s: WriteSingleCoilService = new TcpServiceRequest(payload.transactionId, payload.unitId, req, ctx.channel())
        serviceHandler.onWriteSingleCoil(s)
      }

      case req: WriteSingleRegisterRequest => dispatch {
        val s: WriteSingleRegisterService = new TcpServiceRequest(payload.transactionId, payload.unitId, req, ctx.channel())
        serviceHandler.onWriteSingleRegister(s)
      }

      case req: WriteMultipleCoilsRequest => dispatch {
        val s: WriteMultipleCoilsService = new TcpServiceRequest(payload.transactionId, payload.unitId, req, ctx.channel())
        serviceHandler.onWriteMultipleCoils(s)
      }

      case req: WriteMultipleRegistersRequest => dispatch {
        val s: WriteMultipleRegistersService = new TcpServiceRequest(payload.transactionId, payload.unitId, req, ctx.channel())
        serviceHandler.onWriteMultipleRegisters(s)
      }

      case req: MaskWriteRegisterRequest => dispatch {
        val s: MaskWriteRegisterService = new TcpServiceRequest(payload.transactionId, payload.unitId, req, ctx.channel())
        serviceHandler.onMaskWriteRegister(s)
      }
    }
  }

  private def dispatch(body: => Unit): Unit = {
    executionContext.execute(new Runnable {
      override def run(): Unit = body
    })
  }

}
