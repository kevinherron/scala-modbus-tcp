package com.digitalpetri.modbus

import com.digitalpetri.modbus.FunctionCodes.FunctionCode

/** Base type for ModbusRequests and ModbusResponses */
trait ModbusPdu {

  def functionCode: FunctionCode

}
