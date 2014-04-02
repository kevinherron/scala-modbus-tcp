package com.digitalpetri.modbus.layers

import com.digitalpetri.modbus.serialization.ModbusPduEncoder
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

class ModbusTcpEncoder(encoder: ModbusPduEncoder) extends MessageToByteEncoder[TcpPayload] {

  def encode(ctx: ChannelHandlerContext, payload: TcpPayload, buffer: ByteBuf): Unit = {
    val headerStartIndex = buffer.writerIndex()
    buffer.writeZero(MbapHeader.Length)

    val pduStartIndex = buffer.writerIndex()
    encoder.encode(payload.pdu, buffer)
    val pduLength = buffer.writerIndex() - pduStartIndex

    val header = MbapHeader(
      transactionId = payload.transactionId,
      length        = pduLength + 1,
      unitId        = payload.unitId)

    buffer.markWriterIndex()
    buffer.writerIndex(headerStartIndex)
    MbapHeader.encode(header, buffer)
    buffer.resetWriterIndex()
  }

}
