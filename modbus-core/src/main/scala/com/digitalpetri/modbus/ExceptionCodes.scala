package com.digitalpetri.modbus


sealed abstract case class ExceptionCode(exceptionCode: Int) {

  override def toString = productPrefix + "(" + "0x%02X".format(exceptionCode) + ")"

}

/**
 * The function code received in the query is not an allowable action for the server. This may be because the function
 * code is only applicable to newer devices, and was not implemented in the unit selected. It could also indicate that
 * the server is in the wrong state to process a request of this type, for example because it is un-configured and is
 * being asked to return register values.
 */
object IllegalFunction extends ExceptionCode(0x01)

/**
 * The data address received in the query is not an allowable address for the server. More specifically, the combination
 * of reference number and transfer length is invalid. For a controller with 100 registers, the PDU addresses the first
 * register as 0, and the last one as 99. If a request is submitted with a starting register address of 96 and a
 * quantity of registers of 4, then this request will successfully operate (address-wise at least) on registers 96, 97,
 * 98, 99. If a request is submitted with a starting register address of 96 and a quantity of registers of 5, then this
 * request will fail with Exception Code 0x02 “Illegal Data Address” since it attempts to operate on registers 96, 97,
 * 98, 99 and 100, and there is no register with address 100.
 */
object IllegalDataAddress extends ExceptionCode(0x02)

/**
 * A value contained in the query data field is not an allowable value for server. This indicates a fault in the
 * structure of the remainder of a complex request, such as that the implied length is incorrect. It specifically does
 * NOT mean that a data item submitted for storage in a register has a value outside the expectation of the application
 * program, since the MODBUS protocol is unaware of the significance of any particular value of any particular register.
 */
object IllegalDataValue extends ExceptionCode(0x03)

/**
 * An unrecoverable error occurred while the server was attempting to perform the requested action.
 */
object ServerDeviceFailure extends ExceptionCode(0x04)

/**
 * Specialized use in conjunction with programming commands.
 * <p>
 * The server has accepted the request and is processing it, but a long duration of time will be required to do so. This
 * response is returned to prevent a timeout error from occurring in the client. The client can next issue a Poll Program Complete message to determine if processing is completed.
 */
object Acknowledge extends ExceptionCode(0x05)

/**
 * Specialized use in conjunction with programming commands.
 * <p>
 * The server is engaged in processing a long– duration program command. The client should retransmit the message later
 * when the server is free.
 */
object ServerDeviceBusy extends ExceptionCode(0x06)

/**
 * Specialized use in conjunction with function codes 20 and 21 and reference type 6, to indicate that the extended file
 * area failed to pass a consistency check.
 * <p>
 * The server attempted to read record file, but detected a parity error in the memory. The client can retry the
 * request, but service may be required on the server device.
 */
object MemoryParityError extends ExceptionCode(0x08)

/**
 * Specialized use in conjunction with gateways, indicates that the gateway was unable to allocate an internal
 * communication path from the input port to the output port for processing the request. Usually means that the gateway
 * is mis-configured or overloaded.
 */
object GatewayPathUnavailable extends ExceptionCode(0x0A)

/**
 * Specialized use in conjunction with gateways, indicates that no response was obtained from the target device. Usually
 * means that the device is not present on the network.
 */
object GatewayTargetDeviceFailedToResponse extends ExceptionCode(0x0B)

