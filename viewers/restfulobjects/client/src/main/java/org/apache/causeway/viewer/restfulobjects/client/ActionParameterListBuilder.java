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
package org.apache.causeway.viewer.restfulobjects.client;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.client.Entity;

import lombok.Getter;

/**
 * @since 2.0 {@index}
 */
public class ActionParameterListBuilder {

    private final Map<String, String> actionParameters = new LinkedHashMap<>();

    @Getter
    private final Map<String, Class<?>> actionParameterTypes = new LinkedHashMap<>();

    public ActionParameterListBuilder addActionParameter(final String parameterName, final String parameterValue) {
        actionParameters.put(parameterName, parameterValue != null
                ? value("\"" + parameterValue + "\"")
                : value(JSON_NULL_LITERAL));
        actionParameterTypes.put(parameterName, String.class);
        return this;
    }

    public ActionParameterListBuilder addActionParameter(final String parameterName, final int parameterValue) {
        actionParameters.put(parameterName, value(""+parameterValue));
        actionParameterTypes.put(parameterName, int.class);
        return this;
    }

    public ActionParameterListBuilder addActionParameter(final String parameterName, final long parameterValue) {
        actionParameters.put(parameterName, value(""+parameterValue));
        actionParameterTypes.put(parameterName, long.class);
        return this;
    }

    public ActionParameterListBuilder addActionParameter(final String parameterName, final byte parameterValue) {
        actionParameters.put(parameterName, value(""+parameterValue));
        actionParameterTypes.put(parameterName, byte.class);
        return this;
    }

    public ActionParameterListBuilder addActionParameter(final String parameterName, final short parameterValue) {
        actionParameters.put(parameterName, value(""+parameterValue));
        actionParameterTypes.put(parameterName, short.class);
        return this;
    }

    public ActionParameterListBuilder addActionParameter(final String parameterName, final double parameterValue) {
        actionParameters.put(parameterName, value(""+parameterValue));
        actionParameterTypes.put(parameterName, double.class);
        return this;
    }

    public ActionParameterListBuilder addActionParameter(final String parameterName, final float parameterValue) {
        actionParameters.put(parameterName, value(""+parameterValue));
        actionParameterTypes.put(parameterName, float.class);
        return this;
    }

    public ActionParameterListBuilder addActionParameter(final String parameterName, final boolean parameterValue) {
        actionParameters.put(parameterName, value(""+parameterValue));
        actionParameterTypes.put(parameterName, boolean.class);
        return this;
    }

//XXX would be nice to have, but also requires the RO spec to be updated
//    public ActionParameterListBuilder addActionParameterDto(String parameterName, Object parameterDto) {
//        actionParameters.put(parameterName, dto(parameterDto));
//        return this;
//    }

    public Entity<String> build() {

        final StringBuilder sb = new StringBuilder();
        sb.append("{\n")
        .append(actionParameters.entrySet().stream()
                .map(this::toJson)
                .collect(Collectors.joining(",\n")))
        .append("\n}");

        return Entity.json(sb.toString());
    }

    // -- HELPER

    private static final String JSON_NULL_LITERAL = "null";

    private String value(final String valueLiteral) {
        return "{\"value\" : " + valueLiteral + "}";
    }

    private String toJson(final Map.Entry<String, String> entry) {
        return "   \""+entry.getKey()+"\": "+entry.getValue();
    }

}
