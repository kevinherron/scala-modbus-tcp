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

import io.netty.channel.{ChannelFuture, Channel}
import io.netty.util.concurrent.GenericFutureListener
import java.util.concurrent.atomic.AtomicReference
import scala.concurrent._
import scala.util.{Failure, Success}

object AbstractChannelManager {
  private sealed trait State
  private case object Idle extends State
  private case class Connecting(p: Promise[Channel]) extends State
  private case class Connected(channel: Channel) extends State
}

abstract class AbstractChannelManager {

  import AbstractChannelManager._

  private[this] val state = new AtomicReference[State](Idle)

  def getChannel: Either[Future[Channel], Channel] = {
    state.get match {
      case s@Idle =>
        val p: Promise[Channel] = promise()
        val nextState = Connecting(p)
        if (state.compareAndSet(s, nextState)) Left(connect(nextState, p)) else getChannel

      case s@Connecting(p) =>
        Left(p.future)

      case s@Connected(channel) =>
        Right(channel)
    }
  }

  private[this] def connect(expectedState: State, channelPromise: Promise[Channel]): Future[Channel] = {
    channelPromise.future.onComplete {
      case Success(ch) =>
        if (state.compareAndSet(expectedState, Connected(ch))) {
          ch.closeFuture().addListener(new GenericFutureListener[ChannelFuture] {
            def operationComplete(future: ChannelFuture) {
              state.set(Idle)
            }
          })
        }

      case Failure(ex) => state.compareAndSet(expectedState, Idle)
    }

    connect(channelPromise)

    channelPromise.future
  }

  /**
   * Make a connection, completing the Promise with the resulting Channel.
   */
  def connect(channelPromise: Promise[Channel]): Unit

  /**
   * ExecutionContext to run completion callbacks on.
   */
  implicit val executionContext: ExecutionContext

  def getStatus: String = state.get match {
    case s@Idle           => "Idle"
    case s@Connecting(_)  => "Connecting"
    case s@Connected(_)   => "Connected"
  }

  def disconnect(): Unit = {
    state.get match {
      case s@Connecting(p) => p.future.onSuccess { case ch => ch.close() }
      case s@Connected(ch) => ch.close()
      case s@Idle => // No-op
    }
  }



}
