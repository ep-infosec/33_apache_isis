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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Optional;
import java.util.function.UnaryOperator;

import org.springframework.lang.Nullable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.LineBreak;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import org.apache.causeway.commons.functional.Try;

import lombok.SneakyThrows;
import lombok.val;

/**
 * <h1>- internal use only -</h1>
 * <p>
 * Utilities for the YAML format.
 * </p>
 * <p>
 * <b>WARNING</b>: Do <b>NOT</b> use any of the classes provided by this package! <br/>
 * These may be changed or removed without notice!
 * </p>
 * @since 2.0
 */
public class _Yaml {

    // -- FROM INPUT STREAM

    private static <T> T _readYaml(final Class<T> clazz, final InputStream content) {
        val yaml = new Yaml(new Constructor(clazz));
        return yaml.load(content);
    }

    /**
     * Either deserialize YAML content from given YAML content InputStream into an instance of
     * given {@code clazz} type, or any exception that occurred during parsing.
     * @param <T>
     * @param clazz
     * @param content
     */
    public static <T> Try<T> readYaml(final Class<T> clazz, final InputStream content) {
        return Try.call(()->_readYaml(clazz, content));
    }

    // -- FROM STRING

    private static <T> T _readYaml(final Class<T> clazz, final String content) {
        val yaml = new Yaml(new Constructor(clazz));
        return yaml.load(content);
    }

    /**
     * Either deserialize YAML content from given YAML content String into an instance of
     * given {@code clazz} type, or any exception that occurred during parsing.
     * @param <T>
     * @param clazz
     * @param content
     */
    public static <T> Try<T> readYaml(final Class<T> clazz, final String content) {
        return Try.call(()->_readYaml(clazz, content));
    }

    // -- FROM FILE

    private static <T> T _readYaml(final Class<T> clazz, final File content) throws FileNotFoundException, IOException {
        try(val fis = new FileInputStream(content)) {
            val yaml = new Yaml(new Constructor(clazz));
            return yaml.load(fis);
        }
    }

    /**
     * Either deserialize YAML content from given YAML content File into an instance of
     * given {@code clazz} type, or any exception that occurred during parsing.
     * @param <T>
     * @param clazz
     * @param content
     */
    public static <T> Try<T> readYaml(final Class<T> clazz, final File content) {
        return Try.call(()->_readYaml(clazz, content));
    }

    // -- FROM BYTE ARRAY

    private static <T> T _readYaml(final Class<T> clazz, final byte[] content) throws IOException {
        try(val bais = new ByteArrayInputStream(content)) {
            val yaml = new Yaml(new Constructor(clazz));
            return yaml.load(bais);
        }
    }

    /**
     * Either deserialize YAML content from given YAML content byte[] into an instance of
     * given {@code clazz} type, or any exception that occurred during parsing.
     * @param <T>
     * @param clazz
     * @param content
     */
    public static <T> Try<T> readYaml(final Class<T> clazz, final byte[] content) {
        return Try.call(()->_readYaml(clazz, content));
    }

    // -- WRITING

    public static Try<String> toString(
            final @Nullable Object pojo) {
        return Try.call(()->_toString(pojo, null));
    }

    public static Try<String> toString(
            final @Nullable Object pojo,
            final @Nullable UnaryOperator<DumperOptions> customizer) {
        return Try.call(()->_toString(pojo, customizer));
    }

    @SneakyThrows
    private static String _toString(
            final @Nullable Object pojo,
            final @Nullable UnaryOperator<DumperOptions> customizer) {
        if(pojo==null) {
            return "";
        }
        try(val writer = new StringWriter()){
            val defaultOptions = new DumperOptions();
            defaultOptions.setIndent(2);
            defaultOptions.setLineBreak(LineBreak.UNIX); // fixated for consistency
            //options.setPrettyFlow(true);
            //options.setDefaultFlowStyle(FlowStyle.BLOCK);

            val options = Optional.ofNullable(customizer)
            .map(f->f.apply(defaultOptions))
            .orElse(defaultOptions);

            val yaml = new Yaml(options);
            yaml.dump(pojo, writer);
            return writer.toString();
        }
    }

}
