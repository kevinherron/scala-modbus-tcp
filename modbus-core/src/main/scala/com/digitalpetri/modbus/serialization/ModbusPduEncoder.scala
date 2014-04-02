package com.digitalpetri.modbus.serialization

import com.digitalpetri.modbus.ModbusPdu
import io.netty.buffer.ByteBuf
import scala.util.Try

trait ModbusPduEncoder {

  def encode(pdu: ModbusPdu, buffer: ByteBuf): Try[Unit]

}
