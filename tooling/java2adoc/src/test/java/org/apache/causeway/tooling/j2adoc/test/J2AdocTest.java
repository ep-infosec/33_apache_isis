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
package org.apache.causeway.tooling.j2adoc.test;

import static guru.nidi.codeassert.config.Language.JAVA;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import org.apache.causeway.commons.collections.Can;
import org.apache.causeway.commons.internal.base._Text;
import org.apache.causeway.commons.internal.collections._Sets;
import org.apache.causeway.tooling.j2adoc.J2AdocContext;
import org.apache.causeway.tooling.j2adoc.util.AsciiDocIncludeTagFilter;
import org.apache.causeway.tooling.javamodel.AnalyzerConfigFactory;
import org.apache.causeway.tooling.model4adoc.AsciiDocWriter;

import guru.nidi.codeassert.config.Language;
import lombok.NonNull;
import lombok.val;

class J2AdocTest {

    @Test @Disabled
    void testJavaDoc2AsciiDoc() throws IOException {

        val analyzerConfig = AnalyzerConfigFactory
                .maven(ProjectSampler.apacheCausewayApplib(), Language.JAVA)
                .main();

        val j2aContext = J2AdocContext
                .builder()
//                .javaSourceWithFootnotesFormat()
                //.compactFormat()
                .build();

        analyzerConfig.getSources(JAVA)
        .stream()
        //.filter(source->source.toString().contains("ExecutionMode"))
        //.filter(source->source.toString().contains("FactoryService"))
        //.filter(source->source.toString().contains("Action"))
        //.filter(source->source.toString().contains("SudoService"))
        .filter(source->source.toString().contains("NonRecoverableException"))
        //.peek(source->System.out.println("parsing source: " + source))
        .forEach(j2aContext::add);

        final File tempFile = File.createTempFile("tmp", "adoc");
        j2aContext.streamUnits()
        //.peek(unit->System.err.println("namespace "+unit.getNamespace()))
        .map(unit->unit.toAsciiDoc(j2aContext, tempFile))
        .forEach(adoc->{

            //System.out.println(adoc);

            AsciiDocWriter.print(adoc);
            System.out.println();

        });
    }

    @Test// @Disabled
    void adocDocMining() throws IOException {

        val adocFiles = ProjectSampler.adocFiles(ProjectSampler.apacheCausewayRoot());

        val names = _Sets.<String>newTreeSet();

        Can.ofCollection(adocFiles)
        .stream()

        .filter(source->!source.toString().contains("\\generated\\"))

        //.filter(source->source.toString().contains("XmlSnapshotService"))
        .forEach(file->parseAdoc(file, names::add));

        names.forEach(System.out::println);
    }

    private void parseAdoc(final @NonNull File file, Consumer<String> onName) {
        val lines = _Text.readLinesFromFile(file, StandardCharsets.UTF_8);

        ExampleReferenceFinder.find(
                lines,
                line->line.contains("system:generated:page$")
//               line->line.startsWith("include::")
//                    && line.contains("[tags=")
                    )
        .forEach(exRef->{
            onName.accept(String.format("%s in %s", exRef.name, exRef.matchingLine));
        });
    }

    @Test @Disabled("DANGER!")
    void removeAdocExampleTags() throws IOException {

        val analyzerConfig = AnalyzerConfigFactory
                .maven(ProjectSampler.apacheCausewayApplib(), Language.JAVA)
                .main();

        analyzerConfig.getSources(JAVA)
        .stream()
        .peek(source->System.out.println("parsing source: " + source))
        .filter(source->source.toString().contains("\\causeway\\applib\\"))
        .forEach(AsciiDocIncludeTagFilter::removeAdocExampleTags);

    }

    @Test @Disabled("DANGER!")
    void adocExampleProcessing() throws IOException {

        val adocFiles = ProjectSampler.adocFiles(ProjectSampler.apacheCausewayRoot());

        Can.ofCollection(adocFiles)
        .stream()
        //.filter(source->source.toString().contains("FactoryService"))
        .forEach(ExampleReferenceRewriter::processAdocExampleReferences);
    }


}
