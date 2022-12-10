/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.causeway.client.kroviz.core.aggregator

import org.apache.causeway.client.kroviz.IntegrationTest
import org.apache.causeway.client.kroviz.snapshots.demo2_0_0.ACTIONS_STRINGS
import org.apache.causeway.client.kroviz.snapshots.demo2_0_0.ACTIONS_STRINGS_INVOKE
import org.apache.causeway.client.kroviz.to.Action
import org.apache.causeway.client.kroviz.to.TObject
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ActionDispatcherTest : IntegrationTest() {

    //@Test -> Error: Timeout of 2000ms exceeded. For async tests and hooks, ensure "done()" is called; if returning a Promise, ensure it resolves.
    fun testRestfulServices() {
        if (isAppAvailable()) {
            // given
            val aggregator = ActionDispatcher()
            // when
            val aLogEntry = mockResponse(ACTIONS_STRINGS, aggregator)
            val tLogEntry = mockResponse(ACTIONS_STRINGS_INVOKE, ObjectAggregator(""))

            // then
            assertNotEquals(aLogEntry, tLogEntry)
            assertTrue(aLogEntry.getTransferObject() is Action)
            assertTrue(tLogEntry.getTransferObject() is TObject)
        }
    }

}
