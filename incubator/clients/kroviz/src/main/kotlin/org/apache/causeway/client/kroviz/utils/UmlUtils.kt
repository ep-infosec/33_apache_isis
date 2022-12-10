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
package org.apache.causeway.client.kroviz.utils

import org.apache.causeway.client.kroviz.core.aggregator.SvgDispatcher
import org.apache.causeway.client.kroviz.core.event.ResourceProxy
import org.apache.causeway.client.kroviz.to.Argument
import org.apache.causeway.client.kroviz.to.Link
import org.apache.causeway.client.kroviz.to.Method
import org.apache.causeway.client.kroviz.ui.core.Constants

object UmlUtils {

    fun generateDiagram(plantUmlCode: String, callBack: Any) {
        val args = mutableMapOf<String, Argument>()
        args["diagram_source"] = Argument(key = "\"diagram_source\"", value = plantUmlCode)
        args["diagram_type"] = Argument(key = "\"diagram_type\"", value = "\"plantuml\"")
        args["output_format"] = Argument(key = "\"output_format\"", value = "\"svg\"")

        console.log("[UmlUtils.generateDiagram]")
        console.log(args)

        val link = Link(href = Constants.krokiUrl, method = Method.POST.operation, args = args)
        val agr = SvgDispatcher(callBack)

        ResourceProxy().fetch(link, agr, isRest = false)
    }

    fun generateJsonDiagram(pumlCode: String, callBack: Any) {
        val agr = SvgDispatcher(callBack)
        ResourceProxy().invokeKroki(pumlCode, agr)
    }

}
