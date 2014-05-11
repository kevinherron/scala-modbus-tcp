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
    val code = buffer.readByte()

    if (FunctionCode.isExceptionCode(code)) {
      val functionCode = FunctionCode.fromByte(code - 0x80)
      decodeException(functionCode)(buffer)
    } else {
      val functionCode = FunctionCode.fromByte(code)
      responseDecoder(functionCode)(buffer)
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
    val coilValue   = buffer.readUnsignedShort()

    WriteSingleCoilResponse(coilAddress, coilStatus = coilValue == 0xFF00)
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
    val andMask           = buffer.readUnsignedShort()
    val orMask            = buffer.readUnsignedShort()

    MaskWriteRegisterResponse(referenceAddress, andMask, orMask)
  }

  def decodeException(inner: FunctionCode)(buffer: ByteBuf) = Try {
    val exceptionCode = ExceptionCode.fromByte(buffer.readByte())

    ExceptionResponse(inner, exceptionCode)
  }

  def decodeUnsupported(code: Int)(buffer: ByteBuf) = Try {
    UnsupportedPdu(UnsupportedFunction(code))
  }

  private def byte2Bools(b: Byte) = {
    val bools = new Array[Boolean](8)
    bools(0) = (b & 0x01) != 0
    bools(1) = (b & 0x02) != 0
    bools(2) = (b & 0x04) != 0
    bools(3) = (b & 0x08) != 0
    bools(4) = (b & 0x10) != 0
    bools(5) = (b & 0x20) != 0
    bools(6) = (b & 0x40) != 0
    bools(7) = (b & 0x80) != 0
    bools
  }

}
