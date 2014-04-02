Async/non-blocking Modbus TCP master and slave implementation for Java and Scala.

Quick Start
--------
  ```scala
  val config = new ModbusTcpMasterConfig(host = "localhost")
  val master = new ModbusTcpMaster(config)

  val response: Future[ModbusResponse] = master.sendRequest(ReadHoldingRegistersRequest(0, 10))
  ```
  
  See the examples project for more.
  
Get Help
--------

See the examples project or contact kevinherron@gmail.com for more information.


License
--------

Apache License, Version 2.0
