package com.digitalpetri.modbus.master

import com.digitalpetri.modbus.layers.{ModbusTcpDecoder, ModbusTcpEncoder}
import com.digitalpetri.modbus.master.ModbusTcpMaster.ModbusTcpMasterConfig
import com.digitalpetri.modbus.serialization.{ModbusResponseDecoder, ModbusRequestEncoder}
import io.netty.bootstrap.Bootstrap
import io.netty.channel._
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.logging.{LogLevel, LoggingHandler}
import scala.Some
import scala.concurrent.Promise

class ModbusChannelManager(master: ModbusTcpMaster, config: ModbusTcpMasterConfig) extends AbstractChannelManager {

  implicit val executionContext = config.executionContext

  def connect(channelPromise: Promise[Channel]): Unit = {
    val bootstrap = new Bootstrap

    val initializer = new ChannelInitializer[SocketChannel] {
      def initChannel(channel: SocketChannel) {
        def loggerName(handler: String) = config.instanceId match {
          case Some(instanceId) => s"${classOf[ModbusTcpMaster]}.$instanceId.$handler"
          case None => s"${classOf[ModbusTcpMaster]}.$handler"
        }

        channel.pipeline.addLast(new LoggingHandler(loggerName("ByteLogger"), LogLevel.TRACE))
        channel.pipeline.addLast(new ModbusTcpEncoder(new ModbusRequestEncoder))
        channel.pipeline.addLast(new ModbusTcpDecoder(new ModbusResponseDecoder, config.metricRegistry))
        channel.pipeline.addLast(new LoggingHandler(loggerName("MessageLogger"), LogLevel.TRACE))
        channel.pipeline.addLast(new ModbusTcpResponseDispatcher(master, config.executionContext))
      }
    }

    bootstrap.group(config.eventLoop)
      .channel(classOf[NioSocketChannel])
      .handler(initializer)

    bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Int.box(config.timeout.toMillis.toInt))

    bootstrap.connect(config.host, config.port).addListener(new ChannelFutureListener {
      def operationComplete(future: ChannelFuture): Unit = {
        if (future.isSuccess) {
          channelPromise.success(future.channel())
        } else {
          channelPromise.failure(future.cause())
        }
      }
    })
  }

}
