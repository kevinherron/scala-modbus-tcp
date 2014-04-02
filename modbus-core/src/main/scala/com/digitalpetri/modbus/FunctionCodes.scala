package com.digitalpetri.modbus

import scala.util.{Success, Try}


object FunctionCodes {

  sealed abstract class FunctionCode(val functionCode: Int)

  object ReadCoils              extends FunctionCode(0x01)
  object ReadDiscreteInputs     extends FunctionCode(0x02)
  object ReadHoldingRegisters   extends FunctionCode(0x03)
  object ReadInputRegisters     extends FunctionCode(0x04)
  object WriteSingleCoil        extends FunctionCode(0x05)
  object WriteSingleRegister    extends FunctionCode(0x06)
  object WriteMultipleCoils     extends FunctionCode(0x0F)
  object WriteMultipleRegisters extends FunctionCode(0x10)
  object MaskWriteRegister      extends FunctionCode(0x16)

  /** A catch-all for FunctionCodes not supported by the library or codes that are simply invalid. */
  case class UnsupportedFunction(code: Int) extends FunctionCode(code)

  object FunctionCode {

    def fromByte(b: Byte): Try[FunctionCode] = {
      b match {
        case ReadCoils.functionCode               => Success(ReadCoils)
        case ReadDiscreteInputs.functionCode      => Success(ReadDiscreteInputs)
        case ReadHoldingRegisters.functionCode    => Success(ReadHoldingRegisters)
        case ReadInputRegisters.functionCode      => Success(ReadInputRegisters)
        case WriteSingleCoil.functionCode         => Success(WriteSingleCoil)
        case WriteSingleRegister.functionCode     => Success(WriteSingleRegister)
        case WriteMultipleCoils.functionCode      => Success(WriteMultipleCoils)
        case WriteMultipleRegisters.functionCode  => Success(WriteMultipleRegisters)
        case MaskWriteRegister.functionCode       => Success(MaskWriteRegister)

        case _                                    => Success(UnsupportedFunction(b))
      }
    }

  }

}

