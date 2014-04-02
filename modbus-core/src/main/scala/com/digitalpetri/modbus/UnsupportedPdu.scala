package com.digitalpetri.modbus

import com.digitalpetri.modbus.FunctionCodes.UnsupportedFunction

/**
 * A ModbusPdu representing a request or response not supported by the library at this time.
 * @param functionCode The function code of the unsupported request.
 */
case class UnsupportedPdu(functionCode: UnsupportedFunction) extends ModbusPdu
