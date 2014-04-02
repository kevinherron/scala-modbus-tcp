package com.digitalpetri.modbus.layers

import com.digitalpetri.modbus.ModbusPdu

case class TcpPayload(transactionId: Short, unitId: Short, pdu: ModbusPdu)
