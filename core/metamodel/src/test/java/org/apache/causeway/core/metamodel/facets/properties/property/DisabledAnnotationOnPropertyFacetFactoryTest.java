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
package org.apache.causeway.core.metamodel.facets.properties.property;

import java.lang.reflect.Method;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.causeway.applib.annotation.Editing;
import org.apache.causeway.applib.annotation.Property;
import org.apache.causeway.core.metamodel.facetapi.Facet;
import org.apache.causeway.core.metamodel.facets.AbstractFacetFactoryTest;
import org.apache.causeway.core.metamodel.facets.FacetFactory;
import org.apache.causeway.core.metamodel.facets.FacetFactory.ProcessMethodContext;
import org.apache.causeway.core.metamodel.facets.members.disabled.DisabledFacet;
import org.apache.causeway.core.metamodel.facets.members.disabled.DisabledFacetAbstract;

import lombok.val;

class DisabledAnnotationOnPropertyFacetFactoryTest
extends AbstractFacetFactoryTest {

    private PropertyAnnotationFacetFactory facetFactory;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        facetFactory = new PropertyAnnotationFacetFactory(metaModelContext);
    }

    @Override
    protected void tearDown() throws Exception {
        facetFactory = null;
        super.tearDown();
    }

    private void processEditing(
            final PropertyAnnotationFacetFactory facetFactory, final FacetFactory.ProcessMethodContext processMethodContext) {
        val propertyIfAny = processMethodContext.synthesizeOnMethod(Property.class);
        facetFactory.processEditing(processMethodContext, propertyIfAny);
    }

    public void testDisabledAnnotationPickedUpOnProperty() {
        class Customer {
            @Property(editing = Editing.DISABLED)
            public int getNumberOfOrders() {
                return 0;
            }
        }
        final Method actionMethod = findMethod(Customer.class, "getNumberOfOrders");

        processEditing(facetFactory, ProcessMethodContext
                .forTesting(Customer.class, null, actionMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(DisabledFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof DisabledFacetAbstract);

        final DisabledFacet disabledFacet = (DisabledFacet) facet;
        assertThat(disabledFacet.disabledReason(null), is("Always disabled"));

        assertNoMethodsRemoved();
    }

    public void testDisabledAnnotationWithReason() {
        class Customer {
            @Property(editing = Editing.DISABLED, editingDisabledReason = "Oh no you don't!")
            public int getNumberOfOrders() {
                return 0;
            }
        }
        final Method actionMethod = findMethod(Customer.class, "getNumberOfOrders");

        processEditing(facetFactory, ProcessMethodContext
                .forTesting(Customer.class, null, actionMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(DisabledFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof DisabledFacetAbstract);

        final DisabledFacet disabledFacet = (DisabledFacet) facet;
        assertThat(disabledFacet.disabledReason(null), is("Oh no you don't!"));

        assertNoMethodsRemoved();
    }

}
