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

import java.util.concurrent.ExecutionException

import akka.actor.ActorSystem
import org.scalatest.time.{Milliseconds, Seconds, Span}

import scala.concurrent.Future

class AsyncTestKitLikeSpec(system: ActorSystem) extends AsyncSpecBase(system) {

  def this() = this(ActorSystem("AsyncTestKitLike"))

  val span = Span(150, Milliseconds)

  "receivingA using patience" should {
    behave like asyncReceiveSingleMessage(
      "hello world",
      receivingA[String].map(s => s shouldBe "hello world"),
      receivingA[Double])
  }

  "receivingAn using patience" should {
    behave like asyncReceiveSingleMessage(
      "hello world",
      receivingAn[AnyRef].map(s => s shouldBe "hello world"),
      receivingAn[Int])
  }


  "receivingA using span" should {
    behave like asyncReceiveSingleMessage(
      "hello world",
      receivingA[String](span).map(s => s shouldBe "hello world"),
      receivingA[Double](span))
  }

  "receivingAn using span" should {
    behave like asyncReceiveSingleMessage(
      "hello world",
      receivingAn[AnyRef](span).map(s => s shouldBe "hello world"),
      receivingAn[Int](span))
  }

  "receiving using patience" should {
    behave like asyncReceiveSingleMessage(
      "hello world",
      receiving { case str: String => str },
      receiving { case i: Int => i } )

    "expose exceptions in partial function" in {
      val result = recoverToExceptionIf[IllegalArgumentException] {
        receiving { case _ => throw new IllegalArgumentException }
      }
      testActor ! 12345
      result.map(hasSucceeded)
    }
  }

  "receiving using span" should {
    behave like asyncReceiveSingleMessage(
      "hello world",
      receiving(span) { case str: String => str },
      receiving(span) { case i: Int => i } )

    "expose exceptions in partial function" in {
      val result = recoverToExceptionIf[IllegalArgumentException] {
        receiving(span) { case _ => throw new IllegalArgumentException }
      }
      testActor ! 12345
      result.map(hasSucceeded)
    }
  }

  "receiveMsg using patience" should {
    behave like asyncReceiveSingleMessage(
      "hello world",
      receivingMsg("hello world"),
      receivingMsg("goodbye cruel world"))
    behave like asyncReceiveSingleMessage(
      42,
      receivingMsg(42),
      receivingMsg(57))
  }

  "receiveMsg using span" should {
    behave like asyncReceiveSingleMessage(
      "hello world",
      receivingMsg("hello world", span),
      receivingMsg("goodbye cruel world", span))
    behave like asyncReceiveSingleMessage(
      42,
      receivingMsg(42, span),
      receivingMsg(57, span))
  }

  def asyncReceiveSingleMessage[A, B, C](msg: A, goodReceive: => Future[B], badReceive: => Future[C]): Unit = {
    s"Succeed - $msg" in {
      val result = goodReceive
      testActor ! msg // Send message after future
      result.map(hasSucceeded)
    }

    s"Fail if the message is incorrect - $msg" in {
      val result = recoverToExceptionIf[ExecutionException] {
        badReceive
      } map { ex  =>
        ex.getCause shouldBe an [AssertionError]
      }
      testActor ! msg // Send message after future
      result
    }

    s"Fail if the message times out - $msg" in {
      recoverToExceptionIf[ExecutionException] {
        goodReceive
      } map { ex  =>
        ex.getCause shouldBe an [AssertionError]
      }
    }
  }
}

