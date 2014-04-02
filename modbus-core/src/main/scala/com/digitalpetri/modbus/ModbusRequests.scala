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





