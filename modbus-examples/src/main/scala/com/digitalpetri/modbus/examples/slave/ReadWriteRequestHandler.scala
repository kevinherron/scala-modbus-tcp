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

package com.digitalpetri.modbus.examples.slave

import com.digitalpetri.modbus._
import com.digitalpetri.modbus.slave.ServiceRequest._
import com.digitalpetri.modbus.slave.ServiceRequestHandler
import com.typesafe.scalalogging.slf4j.StrictLogging
import scala.collection.concurrent.TrieMap
import scala.util.Random

class ReadWriteRequestHandler extends ServiceRequestHandler with StrictLogging {

  val registerMap = new TrieMap[Int, Int]()
  val coilMap = new TrieMap[Int, Boolean]()

  def onReadHoldingRegisters(service: ReadHoldingRegistersService): Unit = {
    logger.debug(s"Received ${service.request} for unitId=${service.unitId}")

    val request   = service.request
    val address   = request.startAddress
    val quantity  = request.quantity
    val registers = (address until address + quantity).map {
      case i if i < 1024  => registerMap.getOrElseUpdate(i, i)
      case _              => Random.nextInt()
    }.map(_.toShort)

    service.sendResponse(ReadHoldingRegistersResponse(registers))
  }

  def onReadInputRegisters(service: ReadInputRegistersService): Unit = {
    logger.debug(s"Received ${service.request} for unitId=${service.unitId}")

    val request   = service.request
    val address   = request.startAddress
    val quantity  = request.quantity
    val registers = (address until address + quantity).map(_.toShort)

    service.sendResponse(ReadInputRegistersResponse(registers))
  }

  def onReadCoils(service: ReadCoilsService): Unit = {
    logger.debug(s"Received ${service.request} for unitId=${service.unitId}")

    val request   = service.request
    val address   = request.startAddress
    val quantity  = request.quantity
    val coils     = (address until address + quantity).map {
      case i if i < 1024  => coilMap.getOrElseUpdate(i, if (i % 2 == 0) true else false)
      case _              => Random.nextBoolean()
    }

    service.sendResponse(ReadCoilsResponse(coils))
  }

  def onReadDiscreteInputs(service: ReadDiscreteInputsService): Unit = {
    logger.debug(s"Received ${service.request} for unitId=${service.unitId}")

    val request   = service.request
    val address   = request.startAddress
    val quantity  = request.quantity
    val inputs    = (address until address + quantity).map(i => if (i % 2 == 0) true else false)

    service.sendResponse(ReadDiscreteInputsResponse(inputs))
  }


  def onWriteSingleRegister(service: WriteSingleRegisterService): Unit = {
    val request = service.request
    val address = request.registerAddress
    val value   = request.registerValue

    registerMap += (address -> value)

    service.sendResponse(WriteSingleRegisterResponse(address, value.toShort))
  }

  def onWriteMultipleRegisters(service: WriteMultipleRegistersService): Unit = {
    val request = service.request
    val address = request.startingAddress
    val values  = request.values

    (address until address + values.length).zip(values).foreach {
      case (a, v) => registerMap += (a -> v)
    }

    service.sendResponse(WriteMultipleRegistersResponse(address, values.length))
  }

  def onWriteSingleCoil(service: WriteSingleCoilService): Unit = {
    val request = service.request
    val address = request.coilAddress
    val status  = request.coilStatus

    coilMap += (address -> status)

    service.sendResponse(WriteSingleCoilResponse(address, status))
  }

  def onWriteMultipleCoils(service: WriteMultipleCoilsService): Unit = {
    val request = service.request
    val address = request.startingAddress
    val values  = request.values

    (address until address + values.length).zip(values).foreach {
      case (a, v) => coilMap += (a -> v)
    }

    service.sendResponse(WriteMultipleCoilsResponse(address, values.length))
  }

  def onMaskWriteRegister(service: MaskWriteRegisterService): Unit = {
    service.sendException(IllegalFunction)
  }

}
