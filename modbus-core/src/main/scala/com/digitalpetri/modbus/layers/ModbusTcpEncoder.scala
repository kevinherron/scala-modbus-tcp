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
