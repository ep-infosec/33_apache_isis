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
package org.apache.causeway.client.kroviz.snapshots.simpleapp1_16_0

import org.apache.causeway.client.kroviz.snapshots.Response

object ACTIONS_DOWNLOAD_SWAGGER_SCHEMA_DEFINITION : Response() {
    override val url = "http://localhost:8080/restful/services/causewayApplib.SwaggerServiceMenu/actions/downloadSwaggerSchemaDefinition"
    override val str = """{
  "id" : "downloadSwaggerSchemaDefinition",
  "memberType" : "action",
  "links" : [ {
    "rel" : "self",
    "href" : "http://localhost:8080/restful/services/causewayApplib.SwaggerServiceMenu/actions/downloadSwaggerSchemaDefinition",
    "method" : "GET",
    "type" : "application/json;profile=\"urn:org.restfulobjects:repr-types/object-action\""
  }, {
    "rel" : "up",
    "href" : "http://localhost:8080/restful/services/causewayApplib.SwaggerServiceMenu",
    "method" : "GET",
    "type" : "application/json;profile=\"urn:org.restfulobjects:repr-types/object\"",
    "title" : "Prototyping"
  }, {
    "rel" : "urn:org.restfulobjects:rels/invoke;action=\"downloadSwaggerSchemaDefinition\"",
    "href" : "http://localhost:8080/restful/services/causewayApplib.SwaggerServiceMenu/actions/downloadSwaggerSchemaDefinition/invoke",
    "method" : "GET",
    "type" : "application/json;profile=\"urn:org.restfulobjects:repr-types/object-action\"",
    "arguments" : {
      "filename" : {
        "value" : null
      },
      "visibility" : {
        "value" : null
      },
      "format" : {
        "value" : null
      }
    }
  }, {
    "rel" : "describedby",
    "href" : "http://localhost:8080/restful/domain-types/causewayApplib.SwaggerServiceMenu/actions/downloadSwaggerSchemaDefinition",
    "method" : "GET",
    "type" : "application/json;profile=\"urn:org.restfulobjects:repr-types/action-description\""
  } ],
  "extensions" : {
    "actionType" : "prototype",
    "actionSemantics" : "safe"
  },
  "parameters" : {
    "filename" : {
      "num" : 0,
      "id" : "filename",
      "name" : "Filename",
      "description" : "",
      "default" : "swagger"
    },
    "visibility" : {
      "num" : 1,
      "id" : "visibility",
      "name" : "Visibility",
      "description" : "",
      "choices" : [ "Public", "Private", "Private With Prototyping" ],
      "default" : "Private"
    },
    "format" : {
      "num" : 2,
      "id" : "format",
      "name" : "Format",
      "description" : "",
      "choices" : [ "Json", "Yaml" ],
      "default" : "Yaml"
    }
  }
} """
}
