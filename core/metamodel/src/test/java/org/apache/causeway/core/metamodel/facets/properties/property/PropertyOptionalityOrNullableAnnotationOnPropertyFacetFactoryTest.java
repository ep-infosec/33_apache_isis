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

import org.springframework.lang.Nullable;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.causeway.applib.annotation.Optionality;
import org.apache.causeway.applib.annotation.Property;
import org.apache.causeway.core.metamodel.facetapi.Facet;
import org.apache.causeway.core.metamodel.facets.AbstractFacetFactoryTest;
import org.apache.causeway.core.metamodel.facets.FacetFactory;
import org.apache.causeway.core.metamodel.facets.FacetFactory.ProcessMethodContext;
import org.apache.causeway.core.metamodel.facets.objectvalue.mandatory.MandatoryFacet;
import org.apache.causeway.core.metamodel.facets.properties.property.mandatory.MandatoryFacetForPropertyAnnotation;
import org.apache.causeway.core.metamodel.facets.properties.property.mandatory.MandatoryFacetInvertedByNullableAnnotationOnProperty;

import lombok.val;

class PropertyOptionalityOrNullableAnnotationOnPropertyFacetFactoryTest
extends AbstractFacetFactoryTest {

    private PropertyAnnotationFacetFactory facetFactory;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        facetFactory = new PropertyAnnotationFacetFactory(metaModelContext);
    }

    private void processOptional(
            final PropertyAnnotationFacetFactory facetFactory, final FacetFactory.ProcessMethodContext processMethodContext) {
        val propertyIfAny = processMethodContext.synthesizeOnMethod(Property.class);
        facetFactory.processOptional(processMethodContext, propertyIfAny);
    }

    public void testPropertyAnnotationWithOptionalityPickedUpOnProperty() {

        class Customer {
            @Property(optionality = Optionality.OPTIONAL)
            public String getFirstName() {
                return null;
            }
        }
        final Method method = findMethod(Customer.class, "getFirstName");

        processOptional(facetFactory, ProcessMethodContext
                .forTesting(Customer.class, null, method, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(MandatoryFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof MandatoryFacetForPropertyAnnotation.Optional);
    }

    public void testPropertyAnnotationIgnoredForPrimitiveOnProperty() {

        class Customer {
            @Property(optionality = Optionality.OPTIONAL)
            public int getNumberOfOrders() {
                return 0;
            }
        }
        final Method method = findMethod(Customer.class, "getNumberOfOrders");

        processOptional(facetFactory, ProcessMethodContext
                .forTesting(Customer.class, null, method, methodRemover, facetedMethod));

        assertNotNull(facetedMethod.getFacet(MandatoryFacet.class));
    }

    public void testNullableAnnotationPickedUpOnProperty() {

        class Customer {
            @Nullable
            public String getFirstName() {
                return null;
            }
        }
        final Method method = findMethod(Customer.class, "getFirstName");

        processOptional(facetFactory, ProcessMethodContext
                .forTesting(Customer.class, null, method, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(MandatoryFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof MandatoryFacetInvertedByNullableAnnotationOnProperty);
    }

    public void testNullableAnnotationIgnoredForPrimitiveOnProperty() {

        class Customer {
            @Nullable
            public int getNumberOfOrders() {
                return 0;
            }
        }
        final Method method = findMethod(Customer.class, "getNumberOfOrders");

        processOptional(facetFactory, ProcessMethodContext
                .forTesting(Customer.class, null, method, methodRemover, facetedMethod));

        assertNull(facetedMethod.getFacet(MandatoryFacet.class));
    }

}
