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
package org.apache.causeway.client.kroviz.core

import org.apache.causeway.client.kroviz.to.Argument
import org.apache.causeway.client.kroviz.to.Link
import org.apache.causeway.client.kroviz.to.Method
import org.apache.causeway.client.kroviz.utils.DateHelper
import org.apache.causeway.client.kroviz.utils.StringUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UtilsTest {

    @Test
    fun testDecamel() {
        //given
        val word = "OK"
        val expected = "OK"
        //when
        val actual = StringUtils.deCamel(word)
        //
        assertEquals(expected, actual)
    }

    @Test //@Test  breaks on Travis #153 ???
    fun testDate() {
        // given
        val rawDate: Any = "2020-01-25T13:07:05Z"
        //when
        val dateTime = DateHelper.toDate(rawDate)
        // then
        assertNotNull(dateTime)
        assertEquals(2020, dateTime.getFullYear())
        assertEquals(0, dateTime.getMonth()) // c braintwist strikes again
        assertEquals(14, dateTime.getHours())  // MEZ = GMT + 1
        assertEquals(7, dateTime.getMinutes())  // and again? shouldn't it be 7??
    }

    @Test  //breaks on Travis #152 ? https://travis-ci.com/joerg-rade/kroviz/builds/149958789
    fun test_javaOffsetDateTime() {
        // given
        val rawDate: Any = "20200125T140705.356+0100"
        val expected = "2020-01-25T14:07:05.356+0100"
        //when Then

        val actual = DateHelper.convertJavaOffsetDateTimeToISO(rawDate as String)
        assertEquals(expected, actual)

        val dateTime = DateHelper.toDate(actual)
        assertNotNull(dateTime)
        assertEquals(2020, dateTime.getFullYear())
        assertEquals(0, dateTime.getMonth()) // c braintwist strikes again
        assertEquals(25, dateTime.getDate())
        assertEquals(14, dateTime.getHours())  // MEZ = GMT + 1
        assertEquals(7, dateTime.getMinutes())
    }


    //@Test
    fun test_argumentsAsBody() {
        //given
        val href = "http://localhost:8080/restful/services/causewayApplib.FixtureScriptsDefault/actions/runFixtureScript/invoke"
        val rel = "urn:org.restfulobjects:rels/invoke;action='runFixtureScript'"
        val type = "application/json;profile='urn:org.restfulobjects:repr-types/object-action'"
        val link = Link(method = Method.POST.operation, href = href, rel = rel, type = type)
        //val arguments = LinkedHashMap<String, String>() //
        //link.setArgument(arguments)

        // when
        val body = StringUtils.argumentsAsBody(link)  //TODO link seems to be null

        // then
//        console.log("[UtilsTest.test_argumentsAsBody]")
        console.log(body)
        //TODO add assert
    }

    @Test
    fun testScriptAsBody() {
        //given
        val expected = """"script": {"value": {"href": "http://localhost:8080"}}"""
        val value = "http://localhost:8080"
        val arg = Argument("script", value)
        // when
        val actual = StringUtils.asBody(arg)
        // then
        assertEquals(expected, actual)
    }

    @Test
    fun testParametersAsBody() {
        //given
        val expected = """"parameters": {"value": ""}"""
        val value = ""
        val arg = Argument("parameters", value)
        // when
        val actual = StringUtils.asBody(arg)
        // then
        assertEquals(expected, actual)
    }

}
