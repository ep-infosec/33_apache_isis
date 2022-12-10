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
package org.apache.causeway.valuetypes.asciidoc.applib.value;

import java.util.Optional;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.Options;
import org.asciidoctor.SafeMode;
import org.springframework.lang.Nullable;

import org.apache.causeway.commons.internal.base._Strings;

import lombok.Getter;

final class Converter {

    /**
     * For syntax highlighting to work, the client/browser needs to load specific
     * Javascript and CSS.
     * The framework supports this out of the box with its various viewers,
     * using <i>Prism</i> web-jars.
     *
     * @param adoc - formated input to be converted to HTML
     *
     * @see <a href="https://prismjs.com/">prismjs.com</a>
     */
    public static String adocToHtml(final @Nullable String adoc, final @Nullable Options options) {
        return _Strings.isEmpty(adoc)
                ? ""
                : convert(adoc,
                    Optional
                    .ofNullable(options)
                    .orElseGet(Converter::getDefaultOptions));
    }

    /**
     * Shortcut to {@link #adocToHtml(String, Options)} using default options.
     */
    public static String adocToHtml(final String adoc) {
        return adocToHtml(adoc, null);
    }

    // -- HELPER

    private static String convert(final String content, final Options options) {
        return getAsciidoctor()
            .convert(content, options);
    }

    @Getter(lazy = true)
    private final static Asciidoctor asciidoctor = Asciidoctor.Factory.create();

    @Getter(lazy = true)
    private final static Options defaultOptions = Options.builder()
            .safe(SafeMode.UNSAFE)
            .toFile(false)
            .attributes(Attributes.builder()
                    .sourceHighlighter("prism")
                    .build())
            .build();

}
