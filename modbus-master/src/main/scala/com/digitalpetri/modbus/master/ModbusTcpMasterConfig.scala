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

package com.digitalpetri.modbus.master

import com.codahale.metrics.MetricRegistry
import com.digitalpetri.modbus.Modbus
import io.netty.channel.EventLoopGroup
import io.netty.util.HashedWheelTimer
import java.util.concurrent.{Executor, TimeUnit}
import scala.concurrent.duration.{FiniteDuration, Duration}


case class ModbusTcpMasterConfig(host: String,
                                 port: Int = 502,
                                 timeout: Duration = FiniteDuration(5, TimeUnit.SECONDS),
                                 executor: Executor = Modbus.SharedThreadPool,
                                 eventLoop: EventLoopGroup = Modbus.SharedEventLoop,
                                 wheelTimer: HashedWheelTimer = Modbus.SharedWheelTimer,
                                 metricRegistry: MetricRegistry = Modbus.SharedMetricRegistry,
                                 instanceId: Option[String] = None)

