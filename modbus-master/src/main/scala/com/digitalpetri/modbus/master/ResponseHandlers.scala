package com.digitalpetri.modbus.master

import com.digitalpetri.modbus.ModbusResponse


trait TcpServiceResponseHandler {

  def onServiceResponse(service: TcpServiceResponse)

}

case class TcpServiceResponse(transactionId: Short, unitId: Int, response: ModbusResponse)
