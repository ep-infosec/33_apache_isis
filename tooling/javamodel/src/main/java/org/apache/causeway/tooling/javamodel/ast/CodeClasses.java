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
package org.apache.causeway.tooling.javamodel.ast;

import java.util.function.Predicate;

import org.apache.causeway.commons.internal.base._Strings;

import guru.nidi.codeassert.model.CodeClass;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

@Log4j2
public final class CodeClasses {

    // -- PREDICATES

    public static boolean isSpringStereoType(final @NonNull CodeClass codeClass) {
        return codeClass
        .getAnnotations()
        .stream()
        .map(CodeClass::getName)
        .anyMatch(name->name.startsWith("org.springframework.stereotype."));
    }

    public static Predicate<CodeClass> packageNameStartsWith(final @NonNull String packagePrefix) {
        return codeClass->codeClass.getName().startsWith(packagePrefix);
    }

    public static Predicate<CodeClass> isApacheCausewayPackage() {
        return packageNameStartsWith("org.apache.causeway.");
    }

    public static boolean hasSourceFile(final @NonNull CodeClass codeClass) {
        return _Strings.isNotEmpty(codeClass.getSourceFile())
                && !"Unknown".equals(codeClass.getSourceFile());
    }

    public static boolean hasNoSourceFile(final @NonNull CodeClass codeClass) {
        return !hasSourceFile(codeClass);
    }


    // -- LOGGER

    public static void log(final @NonNull CodeClass codeClass) {
        log.info("codeClass: {}\n"
                + "  methods:{}\n"
                + "  source-file: {}",
                codeClass.getName(),
                MemberInfos.membersToMultilineString(codeClass.getMethods().stream(), "\n    - "),
                codeClass.getSourceFile());
    }

    // -- HELPER


}
