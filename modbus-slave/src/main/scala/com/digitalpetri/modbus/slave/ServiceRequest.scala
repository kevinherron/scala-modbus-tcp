package com.digitalpetri.modbus.slave

import com.digitalpetri.modbus._
import com.digitalpetri.modbus.layers.TcpPayload
import io.netty.channel.Channel


trait ServiceRequest[ReqT <: ModbusRequest, ResT <: ModbusResponse] {
  def unitId: Short
  def request: ReqT
  def sendResponse(response: ResT)
  def sendException(exceptionCode: ExceptionCode)
}

object ServiceRequest {
  type ReadHoldingRegistersService = ServiceRequest[ReadHoldingRegistersRequest, ReadHoldingRegistersResponse]
  type ReadInputRegistersService = ServiceRequest[ReadInputRegistersRequest, ReadInputRegistersResponse]
  type ReadCoilsService = ServiceRequest[ReadCoilsRequest, ReadCoilsResponse]
  type ReadDiscreteInputsService = ServiceRequest[ReadDiscreteInputsRequest, ReadDiscreteInputsResponse]
  type WriteSingleCoilService = ServiceRequest[WriteSingleCoilRequest, WriteSingleCoilResponse]
  type WriteSingleRegisterService = ServiceRequest[WriteSingleRegisterRequest, WriteSingleRegisterResponse]
  type WriteMultipleCoilsService = ServiceRequest[WriteMultipleCoilsRequest, WriteMultipleCoilsResponse]
  type WriteMultipleRegistersService = ServiceRequest[WriteMultipleRegistersRequest, WriteMultipleRegistersResponse]
  type MaskWriteRegisterService = ServiceRequest[MaskWriteRegisterRequest, MaskWriteRegisterResponse]
}

class TcpServiceRequest[ReqT <: ModbusRequest, ResT <: ModbusResponse]
(val transactionId: Short, val unitId: Short, val request: ReqT, channel: Channel) extends ServiceRequest[ReqT, ResT] {

  def sendResponse(response: ResT): Unit = {
    channel.writeAndFlush(TcpPayload(transactionId, unitId, response))
  }

  def sendException(exceptionCode: ExceptionCode): Unit = {
    val response = ExceptionResponse(request.functionCode, exceptionCode)
    channel.writeAndFlush(TcpPayload(transactionId, unitId, response))
  }

}
