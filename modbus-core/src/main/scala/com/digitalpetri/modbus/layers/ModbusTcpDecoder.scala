package com.digitalpetri.modbus.layers

import com.codahale.metrics.MetricRegistry
import com.digitalpetri.modbus._
import com.digitalpetri.modbus.serialization.ModbusPduDecoder
import com.typesafe.scalalogging.slf4j.Logging
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import java.util
import scala.util.Failure
import scala.util.Success

class ModbusTcpDecoder(decoder: ModbusPduDecoder, metrics: MetricRegistry) extends ByteToMessageDecoder with Logging {

  val decodingErrorCounter  = metrics.counter(MetricRegistry.name(getClass, "decoding-error-count"))
  val unsupportedPduCounter = metrics.counter(MetricRegistry.name(getClass, "unsupported-pdu-count"))

  def decode(ctx: ChannelHandlerContext, in: ByteBuf, out: util.List[Object]): Unit = {
    var startIndex = in.readerIndex()

    while (in.readableBytes() >= ModbusTcpDecoder.HeaderLength &&
           in.readableBytes() >= getLength(in, startIndex) + ModbusTcpDecoder.HeaderSize) {

      val payloadTry = for {
        header  <- MbapHeader.decode(in)
        pdu     <- decoder.decode(in)
      } yield {
        TcpPayload(header.transactionId, header.unitId, pdu)
      }

      payloadTry match {
        case Success(payload) =>
          payload.pdu match {
            /*
             * The ModbusPdu is a request or response this library doesn't support. This is not the same as the master
             * or slave implementation not being able to handle the request/response; in this case there isn't even an
             * encoder/decoder implemented for it yet!
             */
            case UnsupportedPdu(functionCode) =>
              unsupportedPduCounter.inc()

              val response = ExceptionResponse(functionCode, IllegalFunction)
              ctx.channel().writeAndFlush(TcpPayload(payload.transactionId, payload.unitId, response))

              // Advance past any bytes we should have read but didn't...
              val endIndex = startIndex + getLength(in, startIndex) + 6
              in.readerIndex(endIndex)

            /*
             * Decoding the header and ModbusPdu was successful; deliver it to the next layer.
             */
            case _ => out.add(payload)
          }

        case Failure(ex) =>
          logger.debug(s"Could not decode header and pdu: ${ex.getMessage}")
          decodingErrorCounter.inc()

          // Advance past any bytes we should have read but didn't...
          val endIndex = startIndex + getLength(in, startIndex) + 6
          in.readerIndex(endIndex)
      }

      startIndex = in.readerIndex()
    }
  }

  private def getLength(in: ByteBuf, startIndex: Int): Int = {
    in.getUnsignedShort(startIndex + ModbusTcpDecoder.LengthFieldIndex)
  }

}

object ModbusTcpDecoder {

  val HeaderLength = MbapHeader.Length
  val HeaderSize = 6
  val LengthFieldIndex = 4

}


