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

import io.netty.buffer.ByteBuf

case class MbapHeader(transactionId: Short, protocolId: Int = 0, length: Int, unitId: Short)

object MbapHeader {

  val Length = 7

  def encode(header: MbapHeader, buffer: ByteBuf): ByteBuf = {
    buffer.writeShort(header.transactionId)
    buffer.writeShort(header.protocolId)
    buffer.writeShort(header.length)
    buffer.writeByte(header.unitId)
  }

  def decode(buffer: ByteBuf): MbapHeader = {
    MbapHeader(
      transactionId = buffer.readShort(),
      protocolId    = buffer.readUnsignedShort(),
      length        = buffer.readUnsignedShort(),
      unitId        = buffer.readUnsignedByte())
  }

}
