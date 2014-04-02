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
