package com.digitalpetri.modbus

import com.digitalpetri.modbus.FunctionCodes._


sealed abstract class ModbusRequest(val functionCode: FunctionCode) extends ModbusPdu

case class MaskWriteRegisterRequest(referenceAddress: Int, andMask: Int, orMask: Int)
  extends ModbusRequest(MaskWriteRegister)

case class ReadCoilsRequest(startAddress: Int, quantity: Int)
  extends ModbusRequest(ReadCoils)

case class ReadDiscreteInputsRequest(startAddress: Int, quantity: Int)
  extends ModbusRequest(ReadDiscreteInputs)

case class ReadHoldingRegistersRequest(startAddress: Int, quantity: Int)
  extends ModbusRequest(ReadHoldingRegisters)

case class ReadInputRegistersRequest(startAddress: Int, quantity: Int)
  extends ModbusRequest(ReadInputRegisters)

case class WriteMultipleCoilsRequest(startingAddress: Int, values: Seq[Boolean])
  extends ModbusRequest(WriteMultipleCoils)

case class WriteMultipleRegistersRequest(startingAddress: Int, values: Seq[Short])
  extends ModbusRequest(WriteMultipleRegisters)

case class WriteSingleCoilRequest(coilAddress: Int, coilStatus: Boolean)
  extends ModbusRequest(WriteSingleCoil)

case class WriteSingleRegisterRequest(registerAddress: Int, registerValue: Int)
  extends ModbusRequest(WriteSingleRegister)





