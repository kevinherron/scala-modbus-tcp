package com.digitalpetri.modbus.serialization

import com.digitalpetri.modbus.ModbusPdu
import io.netty.buffer.ByteBuf
import scala.util.Try

trait ModbusPduDecoder {

  def decode(buffer: ByteBuf): Try[ModbusPdu]

}
