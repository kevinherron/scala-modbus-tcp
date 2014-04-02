package com.digitalpetri.modbus.layers

import io.netty.buffer.ByteBuf
import scala.util.Try

case class MbapHeader(transactionId: Short, protocolId: Int = 0, length: Int, unitId: Short)

object MbapHeader {

  val Length = 7

  def encode(header: MbapHeader, buffer: ByteBuf): Try[Unit] = Try {
    buffer.writeShort(header.transactionId)
    buffer.writeShort(header.protocolId)
    buffer.writeShort(header.length)
    buffer.writeByte(header.unitId)
  }

  def decode(buffer: ByteBuf): Try[MbapHeader] = Try {
    MbapHeader(
      transactionId = buffer.readShort(),
      protocolId    = buffer.readUnsignedShort(),
      length        = buffer.readUnsignedShort(),
      unitId        = buffer.readUnsignedByte())
  }

}
