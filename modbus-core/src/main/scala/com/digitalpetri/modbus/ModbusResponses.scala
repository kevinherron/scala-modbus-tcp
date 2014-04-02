package com.digitalpetri.modbus

import com.digitalpetri.modbus.FunctionCodes._


sealed abstract class ModbusResponse(val functionCode: FunctionCode) extends ModbusPdu

case class ExceptionResponse(override val functionCode: FunctionCode, exceptionCode: ExceptionCode) extends ModbusResponse(functionCode)

case class MaskWriteRegisterResponse(referenceAddress: Int, andMask: Short, orMask: Short)
  extends ModbusResponse(MaskWriteRegister)

case class ReadCoilsResponse(coils: Seq[Boolean])
  extends ModbusResponse(ReadCoils)

case class ReadDiscreteInputsResponse(inputs: Seq[Boolean])
  extends ModbusResponse(ReadDiscreteInputs)

case class ReadHoldingRegistersResponse(registers: Seq[Short])
  extends ModbusResponse(ReadHoldingRegisters)

case class ReadInputRegistersResponse(registers: Seq[Short])
  extends ModbusResponse(ReadInputRegisters)

case class WriteMultipleCoilsResponse(startingAddress: Int, quantity: Int)
  extends ModbusResponse(WriteMultipleCoils)

case class WriteMultipleRegistersResponse(startingAddress: Int, quantity: Int)
  extends ModbusResponse(WriteMultipleRegisters)

case class WriteSingleCoilResponse(coilAddress: Int, coilStatus: Boolean)
  extends ModbusResponse(WriteSingleCoil)

case class WriteSingleRegisterResponse(registerAddress: Int, registerValue: Short)
  extends ModbusResponse(WriteSingleRegister)
