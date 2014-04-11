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

package com.digitalpetri.modbus.serialization

import com.digitalpetri.modbus._
import io.netty.buffer.Unpooled
import org.scalatest.FunSuite
import scala.util.Failure
import scala.util.Success

class ModbusRequestSerializationTest extends FunSuite {

  val encoder = new ModbusRequestEncoder()
  val decoder = new ModbusRequestDecoder()

  test("ReadCoilsRequest is round-trip encodable/decodable") {
    testRoundTrip(ReadCoilsRequest(0, 10))
  }

  test("ReadDiscreteInputsRequest is round-trip encodable/decodable") {
    testRoundTrip(ReadDiscreteInputsRequest(0, 10))
  }

  test("ReadHoldingRegistersRequest is round-trip encodable/decodable") {
    testRoundTrip(ReadHoldingRegistersRequest(0, 10))
  }

  test("ReadInputRegistersRequest is round-trip encodable/decodable") {
    testRoundTrip(ReadInputRegistersRequest(0, 10))
  }

  test("WriteSingleCoilRequest is round-trip encodable/decodable") {
    testRoundTrip(WriteSingleCoilRequest(0, coilStatus = true))
    testRoundTrip(WriteSingleCoilRequest(0, coilStatus = false))
  }

  test("WriteSingleRegisterRequest is round-trip encodable/decodable") {
    testRoundTrip(WriteSingleRegisterRequest(0, 10))
  }

  test("WriteMultipleCoilsRequest is round-trip encodable/decodable") {
    testRoundTrip(WriteMultipleCoilsRequest(0, Seq(true, false, true, false)))
    testRoundTrip(WriteMultipleCoilsRequest(0, Seq(true, true, false, false, true, true, false, false, true)))
    testRoundTrip(WriteMultipleCoilsRequest(10, Seq(true)))
    testRoundTrip(WriteMultipleCoilsRequest(10, Seq(false)))
  }

  test("WriteMultipleRegistersRequest is round-trip encodable/decodable") {
    testRoundTrip(WriteMultipleRegistersRequest(0, Seq(42)))
    testRoundTrip(WriteMultipleRegistersRequest(0, Seq(0, 1, 2, 3)))
  }

  test("MaskWriteRegisterRequest is round-trip encodable/decodable") {
    testRoundTrip(MaskWriteRegisterRequest(0, 0xF0F0, 0x0F0F))
    testRoundTrip(MaskWriteRegisterRequest(10, 0x0000, 0xFFFF))
  }

  private def testRoundTrip(request: ModbusRequest) {
    val buffer = Unpooled.buffer()

    encoder.encode(request, buffer)

    decoder.decode(buffer) match {
      case Success(decoded) => assert(request == decoded)
      case Failure(ex) => fail(ex)
    }
  }

}
