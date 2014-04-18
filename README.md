Async/non-blocking Modbus TCP master and slave implementation for Scala.

Quick Start
--------
  ```scala
  val config = new ModbusTcpMasterConfig(host = "localhost")
  val master = new ModbusTcpMaster(config)

  val request = ReadHoldingRegistersRequest(startAddress = 0, quantity = 10)
  val response = master.sendRequest[ReadHoldingRegistersResponse](request)
  ```
  
  See the examples project for more.
  
Get Help
--------

See the examples project or contact kevinherron@gmail.com for more information.


License
--------

Apache License, Version 2.0
