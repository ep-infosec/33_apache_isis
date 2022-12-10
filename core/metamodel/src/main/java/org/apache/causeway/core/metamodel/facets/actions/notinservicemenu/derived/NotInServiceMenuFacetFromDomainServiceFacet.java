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
package org.apache.causeway.core.metamodel.facets.actions.notinservicemenu.derived;


import java.util.function.BiConsumer;

import org.apache.causeway.applib.annotation.NatureOfService;
import org.apache.causeway.core.metamodel.facetapi.FacetHolder;
import org.apache.causeway.core.metamodel.facets.actions.notinservicemenu.NotInServiceMenuFacetAbstract;
import org.apache.causeway.core.metamodel.interactions.VisibilityContext;


public class NotInServiceMenuFacetFromDomainServiceFacet
extends NotInServiceMenuFacetAbstract {

    private final NatureOfService natureOfService;

    public NotInServiceMenuFacetFromDomainServiceFacet(
            final NatureOfService natureOfService, final FacetHolder holder) {
        super(holder, Precedence.HIGH); // facet has final say, don't override
        this.natureOfService = natureOfService;
    }

    @Override
    public String hides(final VisibilityContext ic) {
        return String.format("@DomainService(nature=%s) annotation present", natureOfService);
    }

    NatureOfService getNatureOfService() {
        return natureOfService;
    }

    @Override
    public void visitAttributes(final BiConsumer<String, Object> visitor) {
        super.visitAttributes(visitor);
        visitor.accept("natureOfService", natureOfService);
    }
}
