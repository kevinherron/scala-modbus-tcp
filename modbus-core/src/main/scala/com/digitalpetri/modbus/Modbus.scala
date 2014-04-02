package com.digitalpetri.modbus

import com.codahale.metrics.MetricRegistry
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.util.HashedWheelTimer

object Modbus {

  lazy val SharedEventLoop = new NioEventLoopGroup()
  lazy val SharedWheelTimer = new HashedWheelTimer()
  lazy val SharedMetricRegistry = new MetricRegistry()

}
