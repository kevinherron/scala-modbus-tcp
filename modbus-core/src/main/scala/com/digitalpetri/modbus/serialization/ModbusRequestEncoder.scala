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

package com.digitalpetri.modbus.serialization

import com.digitalpetri.modbus._
import io.netty.buffer.ByteBuf
import scala.language.implicitConversions
import scala.util.Try


class ModbusRequestEncoder extends ModbusPduEncoder {

  def encode(pdu: ModbusPdu, buffer: ByteBuf): Try[Unit] = {
    pdu.asInstanceOf[ModbusRequest] match {
      case r: ReadCoilsRequest              => encodeReadCoils(r, buffer)
      case r: ReadDiscreteInputsRequest     => encodeReadDiscreteInputs(r, buffer)
      case r: ReadHoldingRegistersRequest   => encodeReadHoldingRegisters(r, buffer)
      case r: ReadInputRegistersRequest     => encodeReadInputRegisters(r, buffer)
      case r: WriteSingleCoilRequest        => encodeWriteSingleCoil(r, buffer)
      case r: WriteSingleRegisterRequest    => encodeWriteSingleRegister(r, buffer)
      case r: WriteMultipleCoilsRequest     => encodeWriteMultipleCoils(r, buffer)
      case r: WriteMultipleRegistersRequest => encodeWriteMultipleRegisters(r, buffer)
      case r: MaskWriteRegisterRequest      => encodeMaskWriteRegister(r, buffer)
    }
  }

  def encodeReadCoils(request: ReadCoilsRequest, buffer: ByteBuf): Try[Unit] = Try {
    buffer.writeByte(request.functionCode.functionCode)
    buffer.writeShort(request.startAddress)
    buffer.writeShort(request.quantity)
  }

  def encodeReadDiscreteInputs(request: ReadDiscreteInputsRequest, buffer: ByteBuf): Try[Unit] = Try {
    buffer.writeByte(request.functionCode.functionCode)
    buffer.writeShort(request.startAddress)
    buffer.writeShort(request.quantity)
  }

  def encodeReadHoldingRegisters(request: ReadHoldingRegistersRequest, buffer: ByteBuf): Try[Unit] = Try {
    buffer.writeByte(request.functionCode.functionCode)
    buffer.writeShort(request.startAddress)
    buffer.writeShort(request.quantity)
  }

  def encodeReadInputRegisters(request: ReadInputRegistersRequest, buffer: ByteBuf): Try[Unit] = Try {
    buffer.writeByte(request.functionCode.functionCode)
    buffer.writeShort(request.startAddress)
    buffer.writeShort(request.quantity)
  }

  def encodeWriteMultipleCoils(request: WriteMultipleCoilsRequest, buffer: ByteBuf): Try[Unit] = Try {
    buffer.writeByte(request.functionCode.functionCode)
    buffer.writeShort(request.startingAddress)

    request.values.sliding(8, 8).map(bits2Int).foreach(buffer.writeByte)
  }

  def encodeWriteMultipleRegisters(request: WriteMultipleRegistersRequest, buffer: ByteBuf): Try[Unit] = Try {
    buffer.writeByte(request.functionCode.functionCode)
    buffer.writeShort(request.startingAddress)

    request.values.foreach(s => buffer.writeShort(s))
  }

  def encodeWriteSingleCoil(request: WriteSingleCoilRequest, buffer: ByteBuf): Try[Unit] = Try {
    buffer.writeByte(request.functionCode.functionCode)
    buffer.writeShort(request.coilAddress)

    val coilStatus = if (request.coilStatus) 0xFF00 else 0x0000
    buffer.writeShort(coilStatus)
  }

  def encodeWriteSingleRegister(request: WriteSingleRegisterRequest, buffer: ByteBuf): Try[Unit] = Try {
    buffer.writeByte(request.functionCode.functionCode)
    buffer.writeShort(request.registerAddress)
    buffer.writeShort(request.registerValue)
  }

  def encodeMaskWriteRegister(request: MaskWriteRegisterRequest, buffer: ByteBuf): Try[Unit] = Try {
    buffer.writeByte(request.functionCode.functionCode)
    buffer.writeShort(request.referenceAddress)
    buffer.writeShort(request.andMask)
    buffer.writeShort(request.orMask)
  }

}
