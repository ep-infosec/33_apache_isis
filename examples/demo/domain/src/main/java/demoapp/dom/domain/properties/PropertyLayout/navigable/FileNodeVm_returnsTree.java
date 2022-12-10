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
package demoapp.dom.domain.properties.PropertyLayout.navigable;

import javax.inject.Inject;

import org.apache.causeway.applib.annotation.Action;
import org.apache.causeway.applib.annotation.ActionLayout;
import org.apache.causeway.applib.annotation.SemanticsOf;
import org.apache.causeway.applib.graph.tree.TreeNode;

import lombok.RequiredArgsConstructor;

//tag::class[]
@Action(
    semantics = SemanticsOf.SAFE
)
@ActionLayout(
        cssClassFa="fa-sitemap",
        position = ActionLayout.Position.PANEL,
        associateWith = "tree")
@RequiredArgsConstructor
public class FileNodeVm_returnsTree {

    private final FileNodeVm fileNodeVm;

    public TreeNode<FileNodeVm> act(){
        return fileTreeNodeService.sessionTree();
    }

    @Inject
    FileTreeNodeService fileTreeNodeService;
}
//end::class[]
