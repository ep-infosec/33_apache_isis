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

import org.apache.causeway.commons.internal.base._Files;
import org.apache.causeway.commons.internal.base._Strings;
import org.apache.causeway.commons.internal.exceptions._Exceptions;
import org.apache.causeway.tooling.projectmodel.ArtifactCoordinates;
import org.apache.causeway.tooling.projectmodel.ProjectNode;
import org.apache.causeway.tooling.projectmodel.ProjectNodeFactory;

import lombok.NonNull;
import lombok.val;

public class GradleSettingsFactory {

    public static GradleSettings generateFromMaven(File projRootFolder, String rootProjectName) {
        val projTree = ProjectNodeFactory.maven(projRootFolder);
        return generateFromMaven(projTree, rootProjectName);
    }

    public static GradleSettings generateFromMaven(ProjectNode projTree, String rootProjectName) {

        val rootPath = _Files.canonicalPath(projTree.getProjectDirectory())
                .orElseThrow(()->_Exceptions.unrecoverable("cannot resolve project root"));

        val gradleSettings = new GradleSettings(rootProjectName);
        val folderByArtifactKey = gradleSettings.getBuildArtifactsByArtifactKey();

        projTree.depthFirst(projModel -> {
            folderByArtifactKey.put(projModel.getArtifactCoordinates(), gradleBuildArtifactFor(projModel, rootPath));
        });

        return gradleSettings;
    }

    // -- HELPER

    private static GradleBuildArtifact gradleBuildArtifactFor(ProjectNode projModel, String rootPath) {
        val name = toCanonicalBuildName(projModel.getArtifactCoordinates());
        val realtivePath = toCanonicalRelativePath(projModel, rootPath);
        return GradleBuildArtifact.of(name, realtivePath, projModel.getProjectDirectory());
    }

    private static String toCanonicalBuildName(final @NonNull ArtifactCoordinates artifactKey) {
        return String.format(":%s:%s", artifactKey.getGroupId(), artifactKey.getArtifactId());
    }

    private static String toCanonicalRelativePath(ProjectNode projModel, String rootPath) {
        val canonicalProjDir = _Files.canonicalPath(projModel.getProjectDirectory())
                .orElseThrow(()->_Exceptions.unrecoverable("cannot resolve relative path"));

        val relativePath = _Files.toRelativePath(rootPath, canonicalProjDir);
        return _Strings.prefix(relativePath.replace('\\', '/'), "/");
    }


}
