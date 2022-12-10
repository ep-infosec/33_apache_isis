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
package org.apache.causeway.tooling.projectmodel.gradle;

import java.io.File;
import java.util.Optional;

import lombok.Value;
import lombok.val;

@Value(staticConstructor = "of")
public class GradleBuildArtifact {
    private final String name;
    private final String realtivePath;
    private final File projectDirectory;

    public final boolean isRoot() {
        return realtivePath.equals("/");
    }

    public final Optional<File> getDefaultBuildFile() {
        val buildFile = new File(getProjectDirectory(), "build.gradle");
        if(buildFile.exists()) {
            return Optional.of(buildFile);
        }
        return Optional.empty();
    }

}
