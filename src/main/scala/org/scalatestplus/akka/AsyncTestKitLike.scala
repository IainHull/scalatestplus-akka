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

import akka.actor.ActorSystem
import akka.testkit.{TestKitBase, TestKit}
import org.scalatest.{AsyncTestSuite, AsyncTestSuiteMixin}

trait AsyncTestKitLike extends TestKitBase with AsyncTestSuiteMixin with ReceivingMsg with
    Receiving with ReceivingA with ReceivingAnyOf with ReceivingAllOf with ReceivingAnyClassOf with
    ReceivingAllClassOf with ReceivingN with ReceivingNoMsg { this: AsyncTestSuite =>

}

