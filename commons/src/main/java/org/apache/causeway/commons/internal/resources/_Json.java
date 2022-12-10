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
package org.apache.causeway.commons.internal.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.UnaryOperator;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

import org.springframework.lang.Nullable;

import org.apache.causeway.commons.functional.Try;

import lombok.SneakyThrows;
import lombok.val;

/**
 * <h1>- internal use only -</h1>
 * <p>
 * Utilities for the JSON format.
 * </p>
 * <p>
 * <b>WARNING</b>: Do <b>NOT</b> use any of the classes provided by this package! <br/>
 * These may be changed or removed without notice!
 * </p>
 * @since 2.0
 */
public class _Json {

    // -- STREAM CONTENT

    private static <T> T _readJson(final Class<T> clazz, final InputStream content)
            throws JsonParseException, JsonMappingException, IOException {

        return new ObjectMapper().readValue(content, clazz);
    }

    /**
     * Either deserialize JSON content from given JSON content InputStream into an instance of
     * given {@code clazz} type, or any exception that occurred during parsing.
     * @param <T>
     * @param clazz
     * @param content
     */
    public static <T> Try<T> readJson(final Class<T> clazz, final InputStream content) {
        return Try.call(()->_readJson(clazz, content));
    }

    private static <T> List<T> _readJsonList(final Class<T> elementType, final InputStream content)
            throws JsonParseException, JsonMappingException, IOException {

        val mapper = new ObjectMapper();
        val listFactory = mapper.getTypeFactory().constructCollectionType(List.class, elementType);
        return mapper.readValue(content, listFactory);
    }

    /**
     * Either deserialize JSON content from given JSON content InputStream into an instance of List
     * with given {@code elementType}, or any exception that occurred during parsing.
     * @param <T>
     * @param clazz
     * @param content
     */
    public static <T> Try<List<T>> readJsonList(final Class<T> clazz, final InputStream content) {
        return Try.call(()->_readJsonList(clazz, content));
    }


    // -- STRING CONTENT

    /**
     * Either deserialize JSON content from given JSON content String into an instance of
     * given {@code clazz} type, or any exception that occurred during parsing.
     * @param <T>
     * @param clazz
     * @param content
     */
    public static <T> Try<T> readJson(
            final Class<T> clazz,
            final String content,
            final JsonCustomizer ... customizers) {
        return Try.call(()->mapper(customizers).readValue(content, clazz));
    }

    private static <T> List<T> _readJsonList(final Class<T> elementType, final String content)
            throws JsonParseException, JsonMappingException, IOException {

        val mapper = new ObjectMapper();
        val listFactory = mapper.getTypeFactory().constructCollectionType(List.class, elementType);
        return mapper.readValue(content, listFactory);
    }

    /**
     * Either deserialize JSON content from given JSON content String into an instance of List
     * with given {@code elementType}, or any exception that occurred during parsing.
     * @param <T>
     * @param clazz
     * @param content
     */
    public static <T> Try<List<T>> readJsonList(final Class<T> clazz, final String content) {
        return Try.call(()->_readJsonList(clazz, content));
    }


    // -- FILE CONTENT

    private static <T> T _readJson(final Class<T> clazz, final File content)
            throws JsonParseException, JsonMappingException, IOException {

        return new ObjectMapper().readValue(content, clazz);
    }

    /**
     * Either deserialize JSON content from given JSON content File into an instance of
     * given {@code clazz} type, or any exception that occurred during parsing.
     * @param <T>
     * @param clazz
     * @param content
     */
    public static <T> Try<T> readJson(final Class<T> clazz, final File content) {
        return Try.call(()->_readJson(clazz, content));
    }

    private static <T> List<T> _readJsonList(final Class<T> elementType, final File content)
            throws JsonParseException, JsonMappingException, IOException {

        val mapper = new ObjectMapper();
        val listFactory = mapper.getTypeFactory().constructCollectionType(List.class, elementType);
        return mapper.readValue(content, listFactory);
    }

    /**
     * Either deserialize JSON content from given JSON content File into an instance of List
     * with given {@code elementType}, or any exception that occurred during parsing.
     * @param <T>
     * @param clazz
     * @param content
     */
    public static <T> Try<List<T>> readJsonList(final Class<T> clazz, final File content) {
        return Try.call(()->_readJsonList(clazz, content));
    }

    // -- BYTE CONTENT

    private static <T> T _readJson(final Class<T> clazz, final byte[] content)
            throws JsonParseException, JsonMappingException, IOException {

        return new ObjectMapper().readValue(content, clazz);
    }

    /**
     * Either deserialize JSON content from given JSON content byte[] into an instance of
     * given {@code clazz} type, or any exception that occurred during parsing.
     * @param <T>
     * @param clazz
     * @param content
     */
    public static <T> Try<T> readJson(final Class<T> clazz, final byte[] content) {
        return Try.call(()->_readJson(clazz, content));
    }

    private static <T> List<T> _readJsonList(final Class<T> elementType, final byte[] content)
            throws JsonParseException, JsonMappingException, IOException {

        val mapper = new ObjectMapper();
        val listFactory = mapper.getTypeFactory().constructCollectionType(List.class, elementType);
        return mapper.readValue(content, listFactory);
    }

    /**
     * Either deserialize JSON content from given JSON content byte[] into an instance of List
     * with given {@code elementType}, or any exception that occurred during parsing.
     * @param <T>
     * @param clazz
     * @param content
     */
    public static <T> Try<List<T>> readJsonList(final Class<T> clazz, final byte[] content) {
        return Try.call(()->_readJsonList(clazz, content));
    }

    // -- WRITING

    @FunctionalInterface
    public static interface JsonCustomizer extends UnaryOperator<ObjectMapper> {};

    /** enable indentation for the underlying generator */
    public static ObjectMapper indentedOutput(final ObjectMapper mapper) {
        return mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /** only properties with non-null values are to be included */
    public static ObjectMapper onlyIncludeNonNull(final ObjectMapper mapper) {
        return mapper.setSerializationInclusion(Include.NON_NULL);
    }

    /** add support for JAXB annotations */
    public static ObjectMapper jaxbAnnotationSupport(final ObjectMapper mapper) {
        return mapper.registerModule(new JaxbAnnotationModule());
    }

    @SneakyThrows
    @Nullable
    public static String toString(
            final @Nullable Object pojo,
            final JsonCustomizer ... customizers) {
        return pojo!=null
                ? mapper(customizers).writeValueAsString(pojo)
                : null;
    }

    // -- GET MAPPER

    private static ObjectMapper mapper(
            final JsonCustomizer ... customizers) {
        var mapper = new ObjectMapper();
        for(JsonCustomizer customizer : customizers) {
            mapper = customizer.apply(mapper);
        }
        return mapper;
    }

}
