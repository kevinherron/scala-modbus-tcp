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

class ModbusRequestDecoder extends ModbusPduDecoder {

  def decode(buffer: ByteBuf): Try[ModbusPdu] = {
    for {
      functionCode  <- FunctionCode.fromByte(buffer.readByte())
      modbusRequest <- requestDecoder(functionCode)(buffer)
    } yield {
      modbusRequest
    }
  }

  private def requestDecoder(functionCode: FunctionCode): ByteBuf => Try[ModbusPdu] = {
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

  /**
   * Decodes a ReadCoilsRequest from `buffer`.
   *
   * @param buffer the ByteBuf to decode from.
   * @return a decoded ReadCoilsRequest.
   */
  def decodeReadCoils(buffer: ByteBuf) = Try {
    val startAddress  = buffer.readUnsignedShort()
    val quantity      = buffer.readUnsignedShort()

    ReadCoilsRequest(startAddress, quantity)
  }

  /**
   * Decodes a ReadDiscreteInputsRequest from `buffer`.
   * @param buffer the ByteBuf to decode from.
   * @return a decoded ReadDiscreteInputsRequest.
   */
  def decodeReadDiscreteInputs(buffer: ByteBuf) = Try {
    val startAddress  = buffer.readUnsignedShort()
    val quantity      = buffer.readUnsignedShort()

    ReadDiscreteInputsRequest(startAddress, quantity)
  }

  /**
   * Decodes a ReadHoldingRegisterRequest from `buffer`.
   * @param buffer the ByteBuf to decode from.
   * @return a decoded ReadHoldingRegistersRequest.
   */
  def decodeReadHoldingRegisters(buffer: ByteBuf) = Try {
    val startAddress  = buffer.readUnsignedShort()
    val quantity      = buffer.readUnsignedShort()

    ReadHoldingRegistersRequest(startAddress, quantity)
  }

  /**
   * Decodes a ReadInputRegistersRequest from `buffer`.
   * @param buffer the ByteBuf to decode from.
   * @return a decoded ReadInputRegistersRequest.
   */
  def decodeReadInputRegisters(buffer: ByteBuf) = Try {
    val startAddress  = buffer.readUnsignedShort()
    val quantity      = buffer.readUnsignedShort()

    ReadInputRegistersRequest(startAddress, quantity)
  }

  /**
   * Decodes a WriteSingleCoilRequest from `buffer`.
   * @param buffer the ByteBuf to decode from.
   * @return a decoded WriteSingleCoilRequest.
   */
  def decodeWriteSingleCoil(buffer: ByteBuf) = Try {
    val coilAddress = buffer.readUnsignedShort()
    val coilValue   = buffer.readUnsignedShort()

    val coilStatus  = (coilValue == 0xFF00)

    WriteSingleCoilRequest(coilAddress, coilStatus)
  }

  /**
   * Decodes a WriteSingleRegisterRequest from `buffer`.
   * @param buffer the ByteBuf to decode from.
   * @return a decoded WriteSingleRegisterRequest.
   */
  def decodeWriteSingleRegister(buffer: ByteBuf) = Try {
    val registerAddress = buffer.readUnsignedShort()
    val registerValue   = buffer.readUnsignedShort()

    WriteSingleRegisterRequest(registerAddress, registerValue)
  }

  /**
   * Decodes a WriteMultipleCoilsRequest from `buffer`.
   * @param buffer the ByteBuf to decode from.
   * @return a decoded WriteMultipleCoilsRequest.
   */
  def decodeWriteMultipleCoils(buffer: ByteBuf) = Try {
    val startingAddress = buffer.readUnsignedShort()
    val quantity        = buffer.readUnsignedShort()
    val byteCount       = buffer.readUnsignedByte()

    val bools = for (i <- 1 to byteCount) yield byte2Bools(buffer.readByte())
    val values = bools.flatten.take(quantity)

    WriteMultipleCoilsRequest(startingAddress, values)
  }

  /**
   * Decodes a WriteMultipleRegistersRequest from `buffer`.
   * @param buffer the ByteBuf to decode from.
   * @return a decoded WriteMultipleRegistersRequest.
   */
  def decodeWriteMultipleRegisters(buffer: ByteBuf) = Try {
    val startingAddress = buffer.readUnsignedShort()
    val quantity        = buffer.readUnsignedShort()
    val byteCount       = buffer.readUnsignedByte()

    assert(quantity == byteCount * 2)

    val values = for (i <- 1 to quantity) yield buffer.readShort()

    WriteMultipleRegistersRequest(startingAddress, values)
  }

  /**
   * Decodes a MaskWriteRegister request from `buffer`.
   * @param buffer the ByteBuf to decode from.
   * @return a decoded MaskWriteRegisterRequest.
   */
  def decodeMaskWriteRegister(buffer: ByteBuf) = Try {
    val referenceAddress = buffer.readUnsignedShort()
    val andMask          = buffer.readUnsignedShort()
    val orMask           = buffer.readUnsignedShort()

    MaskWriteRegisterRequest(referenceAddress, andMask, orMask)
  }

  def decodeUnsupported(code: Int)(buffer: ByteBuf) = Try {
    UnsupportedPdu(UnsupportedFunction(code))
  }

  private def byte2Bools(b: Byte) =
    (0 to 7) map isBitSet(b)

  private def isBitSet(byte: Byte)(bit: Int) =
    ((byte >> bit) & 1) == 1

}
