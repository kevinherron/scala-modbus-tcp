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

import com.codahale.metrics.{Timer, MetricRegistry}
import com.digitalpetri.modbus.layers.TcpPayload
import com.digitalpetri.modbus.{ModbusResponseException, ExceptionResponse, ModbusResponse, ModbusRequest}
import io.netty.channel._
import io.netty.util.{Timeout, TimerTask}
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.{TimeUnit, ConcurrentHashMap}
import org.slf4j.LoggerFactory
import scala.Some
import scala.concurrent.{ExecutionContext, Promise, Future}
import scala.util.{Failure, Success}


class ModbusTcpMaster(config: ModbusTcpMasterConfig) extends TcpServiceResponseHandler {

  private implicit val executionContext = ExecutionContext.fromExecutor(config.executor)

  private val logger = config.instanceId match {
    case Some(instanceId) => LoggerFactory.getLogger(s"${getClass.getName}.$instanceId")
    case None => LoggerFactory.getLogger(getClass)
  }

  private val requestCount      = config.metricRegistry.counter(metricName("request-count"))
  private val responseCount     = config.metricRegistry.counter(metricName("response-count"))
  private val lateResponseCount = config.metricRegistry.counter(metricName("late-response-count"))
  private val timeoutCount      = config.metricRegistry.counter(metricName("timeout-count"))
  private val responseTime      = config.metricRegistry.timer(metricName("response-time"))

  private val promises        = new ConcurrentHashMap[Short, (Promise[ModbusResponse], Timeout, Timer.Context)]()
  private val channelManager  = new ModbusChannelManager(this, config)
  private val transactionId   = new AtomicInteger(0)


  def sendRequest[T <: ModbusResponse](request: ModbusRequest, unitId: Short = 0): Future[T] = {
    val promise = Promise[ModbusResponse]()

    channelManager.getChannel match {
      case Left(fch) => fch.onComplete {
        case Success(ch) => writeToChannel(ch, promise, request, unitId)
        case Failure(ex) => promise.failure(ex)
      }
      case Right(ch) => writeToChannel(ch, promise, request, unitId)
    }

    promise.future.transform(r => r.asInstanceOf[T], ex => ex)
  }

  def disconnect(): Unit = {
    channelManager.disconnect()
    promises.clear()
  }

  /** Writes a request to the channel and flushes it. */
  private def writeToChannel(channel: Channel,
                             promise: Promise[ModbusResponse],
                             request: ModbusRequest,
                             unitId: Short): Unit = {

    val txId = transactionId.getAndIncrement.toShort

    val timeout = config.wheelTimer.newTimeout(
      new TimeoutTask(txId),
      config.timeout.toMillis,
      TimeUnit.MILLISECONDS)

    promises.put(txId, (promise, timeout, responseTime.time()))

    channel.writeAndFlush(TcpPayload(txId, unitId, request))
    requestCount.inc()
  }

  def onServiceResponse(service: TcpServiceResponse): Unit = {
    promises.remove(service.transactionId) match {
      case (p, t, c) =>
        responseCount.inc()
        c.stop()
        t.cancel()

        service.response match {
          case ex: ExceptionResponse    => p.failure(new ModbusResponseException(ex))
          case response: ModbusResponse => p.success(response)
        }

      case null =>
        lateResponseCount.inc()
        logger.debug(s"Received response for unknown transactionId: $service")
    }
  }

  private def metricName(name: String) =
    MetricRegistry.name(classOf[ModbusTcpMaster], config.instanceId.getOrElse(""), name)

  private class TimeoutTask(txId: Short) extends TimerTask {
    def run(timeout: Timeout): Unit = {
      promises.remove(txId) match {
        case (p, t, c) =>
          timeoutCount.inc()
          p.failure(new Exception(s"request timed out after ${config.timeout.toMillis}ms"))

        case null => // Just made it...
      }
    }
  }

}

