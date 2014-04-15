package com.digitalpetri.modbus.serialization

import com.digitalpetri.modbus.FunctionCodes.{ReadInputRegisters, WriteSingleRegister, ReadHoldingRegisters}
import com.digitalpetri.modbus._
import io.netty.buffer.Unpooled
import org.scalatest.FunSuite
import scala.util.Failure
import scala.util.Success

class ModbusResponseSerializationTest extends FunSuite {

  val encoder = new ModbusResponseEncoder()
  val decoder = new ModbusResponseDecoder()

  test("ReadCoilsResponse is round-trip encodable/decodable") {
    /*
     * ReadCoilResponses always get padded with false/0-bits if the requested coil count was not a multiple of 8, so
     * pad to a multiple of 8 every time for the sake of testing equality.
     */

    testRoundTrip(ReadCoilsResponse(Seq(true, false, false, false, false, false ,false, false)))
    testRoundTrip(ReadCoilsResponse(Seq(false, false, false, false, false, false, false, false)))
    testRoundTrip(ReadCoilsResponse(Seq(true, true, false, false, false, false, false, false)))
    testRoundTrip(ReadCoilsResponse(Seq(true, false, true, false, false, false, false, false)))
    testRoundTrip(ReadCoilsResponse(Seq(true, true, false, false, false, false, false, false,
                                        true, true, false, false, false, false, false, false)))
  }

  test("ReadDiscreteInputsResponse is round-trip encodable/decodable") {
    testRoundTrip(ReadDiscreteInputsResponse(Seq(true, false, false, false, false, false ,false, false)))
    testRoundTrip(ReadDiscreteInputsResponse(Seq(false, false, false, false, false, false, false, false)))
    testRoundTrip(ReadDiscreteInputsResponse(Seq(true, true, false, false, false, false, false, false)))
    testRoundTrip(ReadDiscreteInputsResponse(Seq(true, false, true, false, false, false, false, false)))
    testRoundTrip(ReadDiscreteInputsResponse(Seq(true, true, false, false, false, false, false, false,
                                                 true, true, false, false, false, false, false, false)))
  }

  test("ReadHoldingRegistersResponse is round-trip encodable/decodable") {
    testRoundTrip(ReadHoldingRegistersResponse(Seq[Short](0, 0, 0)))
    testRoundTrip(ReadHoldingRegistersResponse(Seq[Short](1, 2, 3)))
    testRoundTrip(ReadHoldingRegistersResponse(Seq[Short](1)))
  }

  test("ReadInputRegistersResponse is round-trip encodable/decodable") {
    testRoundTrip(ReadInputRegistersResponse(Seq[Short](0, 0, 0)))
    testRoundTrip(ReadInputRegistersResponse(Seq[Short](1, 2, 3)))
    testRoundTrip(ReadInputRegistersResponse(Seq[Short](1)))
  }

  test("WriteSingleCoilResponse is round-trip encodable/decodable") {
    testRoundTrip(WriteSingleCoilResponse(0, coilStatus = true))
    testRoundTrip(WriteSingleCoilResponse(0, coilStatus = false))
    testRoundTrip(WriteSingleCoilResponse(1, coilStatus = true))
    testRoundTrip(WriteSingleCoilResponse(1, coilStatus = false))
  }

  test("WriteSingleRegisterResponse is round-trip encodable/decodable") {
    testRoundTrip(WriteSingleRegisterResponse(0, 0))
    testRoundTrip(WriteSingleRegisterResponse(0, 1))
    testRoundTrip(WriteSingleRegisterResponse(1, 0))
    testRoundTrip(WriteSingleRegisterResponse(0, 1))
    testRoundTrip(WriteSingleRegisterResponse(10, Short.MaxValue - 1))
    testRoundTrip(WriteSingleRegisterResponse(10, Short.MaxValue))
  }

  test("WriteMultipleCoilsResponse is round-trip encodable/decodable") {
    testRoundTrip(WriteMultipleCoilsResponse(0, 0))
    testRoundTrip(WriteMultipleCoilsResponse(0, 1))
    testRoundTrip(WriteMultipleCoilsResponse(10, 16))
    testRoundTrip(WriteMultipleCoilsResponse(10, 20))
  }

  test("WriteMultipleRegistersResponse is round-trip encodable/decodable") {
    testRoundTrip(WriteMultipleRegistersResponse(0, 0))
    testRoundTrip(WriteMultipleRegistersResponse(0, 1))
    testRoundTrip(WriteMultipleRegistersResponse(10, 16))
    testRoundTrip(WriteMultipleRegistersResponse(10, 20))
  }

  test("MaskWriteRegisterResponse is round-trip encodable/decodable") {
    testRoundTrip(MaskWriteRegisterResponse(0, 0xFF00, 0x00FF))
    testRoundTrip(MaskWriteRegisterResponse(100, 0x0000, 0xFFFF))
  }

  test("ExceptionResponse is round-trip encodable/decodable") {
    testRoundTrip(ExceptionResponse(ReadHoldingRegisters, IllegalFunction))
    testRoundTrip(ExceptionResponse(WriteSingleRegister, IllegalDataValue))
    testRoundTrip(ExceptionResponse(ReadInputRegisters, IllegalDataAddress))
  }

  private def testRoundTrip(response: ModbusResponse) {
    val buffer = Unpooled.buffer()

    encoder.encode(response, buffer)

    decoder.decode(buffer) match {
      case Success(decoded) => assert(response == decoded)
      case Failure(ex) => fail(ex)
    }
  }
}
