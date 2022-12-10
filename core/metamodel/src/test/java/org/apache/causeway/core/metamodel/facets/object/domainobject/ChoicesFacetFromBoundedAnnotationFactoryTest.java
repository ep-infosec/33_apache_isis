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
package org.apache.causeway.core.metamodel.facets.object.domainobject;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.causeway.applib.annotation.Bounding;
import org.apache.causeway.applib.annotation.DomainObject;
import org.apache.causeway.core.metamodel.facetapi.Facet;
import org.apache.causeway.core.metamodel.facets.AbstractFacetFactoryTest;
import org.apache.causeway.core.metamodel.facets.FacetFactory.ProcessClassContext;
import org.apache.causeway.core.metamodel.facets.object.choices.ChoicesFacetFromBoundedAbstract;
import org.apache.causeway.core.metamodel.facets.objectvalue.choices.ChoicesFacet;

import lombok.val;

class ChoicesFacetFromBoundedAnnotationFactoryTest
extends AbstractFacetFactoryTest {

    private DomainObjectAnnotationFacetFactory facetFactory;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        facetFactory = new DomainObjectAnnotationFacetFactory(metaModelContext);
    }

    @Override
    protected void tearDown() throws Exception {
        facetFactory = null;
        super.tearDown();
    }

    public void testBoundedAnnotationPickedUpOnClass() {
        @DomainObject(bounding = Bounding.BOUNDED)
        class Customer {
        }

        val context = ProcessClassContext
                .forTesting(Customer.class, methodRemover, facetedMethod);
        facetFactory.processBounded(context.synthesizeOnType(DomainObject.class), context);

        final Facet facet = facetedMethod.getFacet(ChoicesFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof ChoicesFacetFromBoundedAbstract);

        assertNoMethodsRemoved();
    }
}
