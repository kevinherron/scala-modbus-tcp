package com.digitalpetri.modbus.examples.slave

import com.digitalpetri.modbus._
import com.digitalpetri.modbus.slave.ServiceRequest._
import com.digitalpetri.modbus.slave.ServiceRequestHandler
import com.typesafe.scalalogging.slf4j.Logging
import scala.util.Random

class ReadOnlyRequestHandler extends ServiceRequestHandler with Logging {

  def onReadHoldingRegisters(service: ReadHoldingRegistersService): Unit = {
    logger.debug(s"Received ${service.request} for unitId=${service.unitId}")

    val request   = service.request
    val quantity  = request.quantity
    val registers = Seq.fill[Short](quantity)(Random.nextInt().toShort)
    val response  = ReadHoldingRegistersResponse(registers)

    service.sendResponse(response)
  }

  def onReadInputRegisters(service: ReadInputRegistersService): Unit = {
    logger.debug(s"Received ${service.request} for unitId=${service.unitId}")

    val request   = service.request
    val quantity  = request.quantity
    val registers = Seq.fill[Short](quantity)(Random.nextInt().toShort)
    val response  = ReadInputRegistersResponse(registers)

    service.sendResponse(response)
  }

  def onReadCoils(service: ReadCoilsService): Unit = {
    logger.debug(s"Received ${service.request} for unitId=${service.unitId}")

    val request   = service.request
    val quantity  = request.quantity
    val coils     = Seq.fill[Boolean](quantity)(Random.nextBoolean())
    val response  = ReadCoilsResponse(coils)

    service.sendResponse(response)
  }

  def onReadDiscreteInputs(service: ReadDiscreteInputsService): Unit = {
    logger.debug(s"Received ${service.request} for unitId=${service.unitId}")

    val request   = service.request
    val quantity  = request.quantity
    val inputs    = Seq.fill[Boolean](quantity)(Random.nextBoolean())
    val response  = ReadDiscreteInputsResponse(inputs)

    service.sendResponse(response)
  }

  def onMaskWriteRegister(service: MaskWriteRegisterService): Unit = {
    service.sendException(IllegalFunction)
  }

  def onWriteMultipleRegisters(service: WriteMultipleRegistersService): Unit = {
    service.sendException(IllegalFunction)
  }

  def onWriteMultipleCoils(service: WriteMultipleCoilsService): Unit = {
    service.sendException(IllegalFunction)
  }

  def onWriteSingleRegister(service: WriteSingleRegisterService): Unit = {
    service.sendException(IllegalFunction)
  }

  def onWriteSingleCoil(service: WriteSingleCoilService): Unit = {
    service.sendException(IllegalFunction)
  }
  
}
