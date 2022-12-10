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
package org.apache.causeway.client.kroviz.snapshots.demo2_0_0

import org.apache.causeway.client.kroviz.snapshots.Response

object ACTIONS_STRINGS : Response() {
    override val url = "http://localhost:8080/restful/objects/demo.JavaLangTypesMenu/1/actions/strings"
    override val str = """
{
  "id" : "strings",
  "memberType" : "action",
  "links" : [ {
    "rel" : "self",
    "href" : "https://localhost:8080/restful/objects/demo.JavaLangTypesMenu/1/actions/strings",
    "method" : "GET",
    "type" : "application/json;profile=\"urn:org.restfulobjects:repr-types/object-action\""
  }, {
    "rel" : "up",
    "href" : "https://localhost:8080/restful/objects/demo.JavaLangTypesMenu/1",
    "method" : "GET",
    "type" : "application/json;profile=\"urn:org.restfulobjects:repr-types/object\"",
    "title" : "JavaLangTypes"
  }, {
    "rel" : "urn:org.restfulobjects:rels/invoke;action=\"strings\"",
    "href" : "https://localhost:8080/restful/objects/demo.JavaLangTypesMenu/1/actions/strings/invoke",
    "method" : "GET",
    "type" : "application/json;profile=\"urn:org.restfulobjects:repr-types/object-action\"",
    "arguments" : { }
  }, {
    "rel" : "describedby",
    "href" : "https://localhost:8080/restful/domain-types/demo.JavaLangTypesMenu/actions/strings",
    "method" : "GET",
    "type" : "application/json;profile=\"urn:org.restfulobjects:repr-types/action-description\""
  } ],
  "extensions" : {
    "actionType" : "user",
    "actionSemantics" : "safe"
  },
  "parameters" : { }
}
"""
}
