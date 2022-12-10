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
package org.apache.causeway.viewer.wicket.model.links;

import java.util.List;

import org.apache.wicket.model.ChainingModel;

import org.apache.causeway.commons.collections.Can;

import lombok.NonNull;
import lombok.val;

public class ListOfLinksModel
extends ChainingModel<List<LinkAndLabel>> {

    private static final long serialVersionUID = 1L;

    public ListOfLinksModel(final @NonNull Can<LinkAndLabel> links) {
        super(links);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<LinkAndLabel> getObject() {
        return ((Can<LinkAndLabel>)super.getTarget()).toList();
    }

    public boolean hasAnyVisibleLink() {
        for (val linkAndLabel : getObject()) {
            if(linkAndLabel.getUiComponent().isVisible()) {
                return true;
            }
        }
        return false;
    }


}
