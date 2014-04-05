package com.digitalpetri.modbus

class ModbusResponseException(val response: ExceptionResponse)
  extends Exception(s"functionCode=${response.functionCode}, exceptionCode=${response.exceptionCode}")
