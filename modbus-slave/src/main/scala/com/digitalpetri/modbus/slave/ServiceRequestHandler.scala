package com.digitalpetri.modbus.slave

import com.digitalpetri.modbus.IllegalFunction
import com.digitalpetri.modbus.slave.ServiceRequest._


trait ServiceRequestHandler {

  def onReadHoldingRegisters(service: ReadHoldingRegistersService)

  def onReadInputRegisters(service: ReadInputRegistersService)

  def onReadCoils(service: ReadCoilsService)

  def onReadDiscreteInputs(service: ReadDiscreteInputsService)

  def onWriteSingleCoil(service: WriteSingleCoilService)

  def onWriteSingleRegister(service: WriteSingleRegisterService)

  def onWriteMultipleCoils(service: WriteMultipleCoilsService)

  def onWriteMultipleRegisters(service: WriteMultipleRegistersService)

  def onMaskWriteRegister(service: MaskWriteRegisterService)

}

/**
 * A ServiceRequestHandler that responds to all requests with an IllegalFunction exception response.
 */
object IllegalFunctionHandler extends ServiceRequestHandler {

  def onReadHoldingRegisters(service: ServiceRequest.ReadHoldingRegistersService): Unit = {
    service.sendException(IllegalFunction)
  }

  def onReadDiscreteInputs(service: ServiceRequest.ReadDiscreteInputsService): Unit = {
    service.sendException(IllegalFunction)
  }

  def onWriteMultipleRegisters(service: ServiceRequest.WriteMultipleRegistersService): Unit = {
    service.sendException(IllegalFunction)
  }

  def onWriteMultipleCoils(service: ServiceRequest.WriteMultipleCoilsService): Unit = {
    service.sendException(IllegalFunction)
  }

  def onReadCoils(service: ServiceRequest.ReadCoilsService): Unit = {
    service.sendException(IllegalFunction)
  }

  def onReadInputRegisters(service: ServiceRequest.ReadInputRegistersService): Unit = {
    service.sendException(IllegalFunction)
  }

  def onMaskWriteRegister(service: ServiceRequest.MaskWriteRegisterService): Unit = {
    service.sendException(IllegalFunction)
  }

  def onWriteSingleCoil(service: ServiceRequest.WriteSingleCoilService): Unit = {
    service.sendException(IllegalFunction)
  }

  def onWriteSingleRegister(service: ServiceRequest.WriteSingleRegisterService): Unit = {
    service.sendException(IllegalFunction)
  }

}


