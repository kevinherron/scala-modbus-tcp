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
