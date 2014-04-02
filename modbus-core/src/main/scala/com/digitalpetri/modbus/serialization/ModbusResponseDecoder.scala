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

import com.digitalpetri.modbus.FunctionCodes._
import com.digitalpetri.modbus._
import io.netty.buffer.ByteBuf
import scala.util.Try

class ModbusResponseDecoder extends ModbusPduDecoder {

  def decode(buffer: ByteBuf): Try[ModbusPdu] = {
    for {
      functionCode    <- FunctionCode.fromByte(buffer.readByte())
      modbusResponse  <- responseDecoder(functionCode)(buffer)
    } yield {
      modbusResponse
    }
  }

  private def responseDecoder(functionCode: FunctionCode): ByteBuf => Try[ModbusPdu] = {
    functionCode match {
      case ReadCoils                  => decodeReadCoils
      case ReadDiscreteInputs         => decodeReadDiscreteInputs
      case ReadHoldingRegisters       => decodeReadHoldingRegisters
      case ReadInputRegisters         => decodeReadInputRegisters
      case WriteSingleCoil            => decodeWriteSingleCoil
      case WriteSingleRegister        => decodeWriteSingleRegister
      case WriteMultipleCoils         => decodeWriteMultipleCoils
      case WriteMultipleRegisters     => decodeWriteMultipleRegisters
      case MaskWriteRegister          => decodeMaskWriteRegister
      case UnsupportedFunction(code)  => decodeUnsupported(code)
    }
  }

  def decodeReadCoils(buffer: ByteBuf) = Try {
    val byteCount = buffer.readUnsignedByte()

    val bools = for (i <- 1 to byteCount) yield byte2Bools(buffer.readByte())
    val coils = bools.flatten

    ReadCoilsResponse(coils)
  }

  def decodeReadDiscreteInputs(buffer: ByteBuf) = Try {
    val byteCount = buffer.readUnsignedByte()

    val bools = for (i <- 1 to byteCount) yield byte2Bools(buffer.readByte())
    val inputs = bools.flatten

    ReadDiscreteInputsResponse(inputs)
  }

  def decodeReadHoldingRegisters(buffer: ByteBuf) = Try {
    val byteCount = buffer.readUnsignedByte()

    val quantity = byteCount / 2
    val registers = for (i <- 1 to quantity) yield buffer.readShort()

    ReadHoldingRegistersResponse(registers)
  }

  def decodeReadInputRegisters(buffer: ByteBuf) = Try {
    val byteCount = buffer.readUnsignedByte()

    val quantity = byteCount / 2
    val registers = for (i <- 1 to quantity) yield buffer.readShort()

    ReadInputRegistersResponse(registers)
  }

  def decodeWriteSingleCoil(buffer: ByteBuf) = Try {
    val coilAddress = buffer.readUnsignedShort()
    val coilStatus  = buffer.readShort() > 0

    WriteSingleCoilResponse(coilAddress, coilStatus = coilStatus)
  }

  def decodeWriteSingleRegister(buffer: ByteBuf) = Try {
    val registerAddress = buffer.readUnsignedShort()
    val registerValue   = buffer.readShort()

    WriteSingleRegisterResponse(registerAddress, registerValue)
  }

  def decodeWriteMultipleCoils(buffer: ByteBuf) = Try {
    val startingAddress = buffer.readUnsignedShort()
    val quantity        = buffer.readUnsignedShort()

    WriteMultipleCoilsResponse(startingAddress, quantity)
  }

  def decodeWriteMultipleRegisters(buffer: ByteBuf) = Try {
    val startingAddress = buffer.readUnsignedShort()
    val quantity        = buffer.readUnsignedShort()

    WriteMultipleRegistersResponse(startingAddress, quantity)
  }

  def decodeMaskWriteRegister(buffer: ByteBuf) = Try {
    val referenceAddress  = buffer.readUnsignedShort()
    val andMask           = buffer.readShort()
    val orMask            = buffer.readShort()

    MaskWriteRegisterResponse(referenceAddress, andMask, orMask)
  }

  def decodeUnsupported(code: Int)(buffer: ByteBuf) = Try {
    UnsupportedPdu(UnsupportedFunction(code))
  }

  private def byte2Bools(b: Byte) =
    (0 to 7) map isBitSet(b)

  private def isBitSet(byte: Byte)(bit: Int) =
    ((byte >> bit) & 1) == 1

}
