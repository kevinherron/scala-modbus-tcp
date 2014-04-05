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

package com.digitalpetri.modbus


object FunctionCodes {

  sealed abstract class FunctionCode(val functionCode: Int)

  case object ReadCoils              extends FunctionCode(0x01)
  case object ReadDiscreteInputs     extends FunctionCode(0x02)
  case object ReadHoldingRegisters   extends FunctionCode(0x03)
  case object ReadInputRegisters     extends FunctionCode(0x04)
  case object WriteSingleCoil        extends FunctionCode(0x05)
  case object WriteSingleRegister    extends FunctionCode(0x06)
  case object WriteMultipleCoils     extends FunctionCode(0x0F)
  case object WriteMultipleRegisters extends FunctionCode(0x10)
  case object MaskWriteRegister      extends FunctionCode(0x16)

  /** A catch-all for FunctionCodes not supported by the library or codes that are simply invalid. */
  case class UnsupportedFunction(code: Int) extends FunctionCode(code)

  object FunctionCode {

    def fromByte(code: Int): FunctionCode = fromByte(code.asInstanceOf[Byte])

    def fromByte(code: Byte): FunctionCode = {
      code match {
        case ReadCoils.functionCode               => ReadCoils
        case ReadDiscreteInputs.functionCode      => ReadDiscreteInputs
        case ReadHoldingRegisters.functionCode    => ReadHoldingRegisters
        case ReadInputRegisters.functionCode      => ReadInputRegisters
        case WriteSingleCoil.functionCode         => WriteSingleCoil
        case WriteSingleRegister.functionCode     => WriteSingleRegister
        case WriteMultipleCoils.functionCode      => WriteMultipleCoils
        case WriteMultipleRegisters.functionCode  => WriteMultipleRegisters
        case MaskWriteRegister.functionCode       => MaskWriteRegister

        case _ => UnsupportedFunction(code)
      }
    }

    def isExceptionCode(code: Byte): Boolean = {
      !fromByte(code - 0x80).isInstanceOf[UnsupportedFunction]
    }

  }

}

