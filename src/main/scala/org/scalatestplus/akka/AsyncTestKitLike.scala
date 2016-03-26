/*
 * Copyright 2016 Artima, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.scalatestplus.akka

import java.util.concurrent.TimeUnit

import akka.testkit.TestKitBase
import org.scalatest.time.Span
import org.scalatest._

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration
import scala.reflect.ClassTag

trait AsyncTestKitLike extends TestKitBase with AsyncTestSuiteMixin  with
  ReceivingAnyOf with ReceivingAllOf with ReceivingAnyClassOf with
  ReceivingAllClassOf with ReceivingN with ReceivingNoMsg {
  this: AsyncTestSuite =>

  /**
    * An object which is an instance of the given type (after erasure) must be received within
    * the allotted time frame; the object will be returned.
    *
    * @tparam T the type of the expected message
    * @param config the patience controlling the time before the `Future` timeouts out
    * @return a `Future` of the received message
    */
  def receivingA[T: ClassTag](implicit config: PatienceConfig): Future[T] = {
    val fd = FiniteDuration(config.timeout.length, config.timeout.unit)
    Future(expectMsgType[T](fd))
  }

  /**
    * Synonym for `receivingA`.
    */
  def receivingAn[T: ClassTag](implicit config: PatienceConfig): Future[T] = {
    val fd = FiniteDuration(config.timeout.length, config.timeout.unit)
    Future(expectMsgType[T](fd))
  }

  /**
    * An object which is an instance of the given type (after erasure) must be received within
    * the allotted time frame; the object will be returned.
    *
    * @tparam T the type of the expected message
    * @param timeout the `Span` before the `Future` timeouts out
    * @return a `Future` of the received message
    */
  def receivingA[T: ClassTag](timeout: Span): Future[T] = Future {
    expectMsgType[T](asFiniteDuration(timeout))
  }

  /**
    * Synonym for `receivingA`.
    */
  def receivingAn[T: ClassTag](span: Span): Future[T] = {
    val fd = FiniteDuration(span.length, span.unit)
    Future(expectMsgType[T](fd))
  }


  /**
    * Within the given time period, a message must be received and the given partial function
    * must be defined for that message; the result from applying the partial function to the
    * received message is returned. The duration may be left unspecified (empty parentheses are
    * required in this case) to use the deadline from the innermost enclosing within block instead.
    *
    * Use this variant to implement more complicated or conditional
    * processing.
    *
    * @tparam T the type of the value returned by `pf`
    * @param pf     the partial function used to verify and transform the message
    * @param config the patience controlling the time before the `Future` timeouts out
    * @return a `Future` of the received message as transformed by the partial function
    */
  def receiving[T](pf: PartialFunction[Any, T])(implicit config: PatienceConfig): Future[T] = {
    receiving(config.timeout)(pf)
  }

  /**
    * Within the given time period, a message must be received and the given partial function
    * must be defined for that message; the result from applying the partial function to the
    * received message is returned. The duration may be left unspecified (empty parentheses are
    * required in this case) to use the deadline from the innermost enclosing within block instead.
    *
    * Use this variant to implement more complicated or conditional
    * processing.
    *
    * @tparam T the type of the value returned by `pf`
    * @param pf      the partial function used to verify and transform the message
    * @param timeout the `Span` before the `Future` timeouts out
    * @return a `Future` of the received message as transformed by the partial function
    */
  def receiving[T](timeout: Span)(pf: PartialFunction[Any, T]): Future[T] = Future {
    expectMsgPF[T](asFiniteDuration(timeout))(pf)
  }

  /**
    * The given message object must be received within the specified time; the object will be returned.
    *
    * @tparam T the type of the value expected
    * @param msg the expected message
    * @param config the patience controlling the time before the `Future` timeouts out
    * @return a `Future` of the received message
    */
  def receivingMsg[T](msg: T)(implicit config: PatienceConfig): Future[T] = {
    receivingMsg(msg, config.timeout)
  }

  /**
    * The given message object must be received within the specified time; the object will be returned.
    *
    * @tparam T the type of the value expected
    * @param msg the expected message
    * @param timeout the `Span` before the `Future` timeouts out
    * @return a `Future` of the received message
    */
  def receivingMsg[T](msg: T, timeout: Span): Future[T] = Future {
    expectMsg(asFiniteDuration(timeout), msg)
  }

  /**
    * Simple function to turn any future returned by `receive*` method into an assertion required to
    * terminate a test case
    *
    * {{{
    * "An echo actor" should {
    *   "reply with a message of the same type" in {
    *     echo ! "hello world"
    *     receivingA[String].map(hasSucceeded)
    *   }
    * }
    * }}}
    *
    * @tparam T
    * @return
    */
  def hasSucceeded[T]: T => Assertion = { _:T => Succeeded }


  private def asFiniteDuration(timeout: Span): FiniteDuration = {
    FiniteDuration(timeout.length, timeout.unit)
  }
}

