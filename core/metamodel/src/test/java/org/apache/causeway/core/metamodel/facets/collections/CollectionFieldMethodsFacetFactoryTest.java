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
package org.apache.causeway.core.metamodel.facets.collections;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.causeway.core.metamodel.facetapi.Facet;
import org.apache.causeway.core.metamodel.facets.AbstractFacetFactoryTest;
import org.apache.causeway.core.metamodel.facets.FacetFactory.ProcessMethodContext;
import org.apache.causeway.core.metamodel.facets.collections.accessor.CollectionAccessorFacetViaAccessor;
import org.apache.causeway.core.metamodel.facets.collections.accessor.CollectionAccessorFacetViaAccessorFactory;
import org.apache.causeway.core.metamodel.facets.propcoll.accessor.PropertyOrCollectionAccessorFacet;

import lombok.val;

class CollectionFieldMethodsFacetFactoryTest
extends AbstractFacetFactoryTest {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testPropertyAccessorFacetIsInstalledForJavaUtilCollectionAndMethodRemoved() {
        val facetFactory = new CollectionAccessorFacetViaAccessorFactory(metaModelContext);

        class Customer {
            @SuppressWarnings({ "rawtypes", "unused" })
            public Collection getOrders() {
                return null;
            }
        }
        final Method collectionAccessorMethod = findMethod(Customer.class, "getOrders");

        facetFactory.process(ProcessMethodContext
                .forTesting(Customer.class, null, collectionAccessorMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(PropertyOrCollectionAccessorFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof CollectionAccessorFacetViaAccessor);
        final CollectionAccessorFacetViaAccessor propertyAccessorFacetViaAccessor = (CollectionAccessorFacetViaAccessor) facet;
        assertEquals(collectionAccessorMethod, propertyAccessorFacetViaAccessor.getMethods().getFirstOrFail());

        assertTrue(methodRemover.getRemovedMethodMethodCalls().contains(collectionAccessorMethod));
    }


    public void testPropertyAccessorFacetIsInstalledForJavaUtilListAndMethodRemoved() {
        val facetFactory = new CollectionAccessorFacetViaAccessorFactory(metaModelContext);

        class Customer {
            @SuppressWarnings({ "rawtypes", "unused" })
            public List getOrders() {
                return null;
            }
        }
        final Method collectionAccessorMethod = findMethod(Customer.class, "getOrders");


        facetFactory.process(ProcessMethodContext
                .forTesting(Customer.class, null, collectionAccessorMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(PropertyOrCollectionAccessorFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof CollectionAccessorFacetViaAccessor);
        final CollectionAccessorFacetViaAccessor propertyAccessorFacetViaAccessor = (CollectionAccessorFacetViaAccessor) facet;
        assertEquals(collectionAccessorMethod, propertyAccessorFacetViaAccessor.getMethods().getFirstOrFail());

        assertTrue(methodRemover.getRemovedMethodMethodCalls().contains(collectionAccessorMethod));
    }

    public void testPropertyAccessorFacetIsInstalledForJavaUtilSetAndMethodRemoved() {
        val facetFactory = new CollectionAccessorFacetViaAccessorFactory(metaModelContext);

        class Customer {
            @SuppressWarnings({ "rawtypes", "unused" })
            public Set getOrders() {
                return null;
            }
        }
        final Method collectionAccessorMethod = findMethod(Customer.class, "getOrders");

        facetFactory.process(ProcessMethodContext
                .forTesting(Customer.class, null, collectionAccessorMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(PropertyOrCollectionAccessorFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof CollectionAccessorFacetViaAccessor);
        final CollectionAccessorFacetViaAccessor propertyAccessorFacetViaAccessor = (CollectionAccessorFacetViaAccessor) facet;
        assertEquals(collectionAccessorMethod, propertyAccessorFacetViaAccessor.getMethods().getFirstOrFail());

        assertTrue(methodRemover.getRemovedMethodMethodCalls().contains(collectionAccessorMethod));
    }

    public void testPropertyAccessorFacetIsInstalledForObjectArrayAndMethodRemoved() {
        val facetFactory = new CollectionAccessorFacetViaAccessorFactory(metaModelContext);

        class Customer {
            @SuppressWarnings("unused")
            public Object[] getOrders() {
                return null;
            }
        }
        final Method collectionAccessorMethod = findMethod(Customer.class, "getOrders");

        facetFactory.process(ProcessMethodContext
                .forTesting(Customer.class, null, collectionAccessorMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(PropertyOrCollectionAccessorFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof CollectionAccessorFacetViaAccessor);
        final CollectionAccessorFacetViaAccessor propertyAccessorFacetViaAccessor = (CollectionAccessorFacetViaAccessor) facet;
        assertEquals(collectionAccessorMethod, propertyAccessorFacetViaAccessor.getMethods().getFirstOrFail());

        assertTrue(methodRemover.getRemovedMethodMethodCalls().contains(collectionAccessorMethod));
    }

    public void testPropertyAccessorFacetIsInstalledForOrderArrayAndMethodRemoved() {
        val facetFactory = new CollectionAccessorFacetViaAccessorFactory(metaModelContext);

        @SuppressWarnings("hiding")
        class Order {
        }
        class Customer {
            @SuppressWarnings("unused")
            public Order[] getOrders() {
                return null;
            }
        }
        final Method collectionAccessorMethod = findMethod(Customer.class, "getOrders");

        facetFactory.process(ProcessMethodContext
                .forTesting(Customer.class, null, collectionAccessorMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(PropertyOrCollectionAccessorFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof CollectionAccessorFacetViaAccessor);
        final CollectionAccessorFacetViaAccessor propertyAccessorFacetViaAccessor = (CollectionAccessorFacetViaAccessor) facet;
        assertEquals(collectionAccessorMethod, propertyAccessorFacetViaAccessor.getMethods().getFirstOrFail());

        assertTrue(methodRemover.getRemovedMethodMethodCalls().contains(collectionAccessorMethod));
    }

    public void testMethodFoundInSuperclass() {
        val facetFactory = new CollectionAccessorFacetViaAccessorFactory(metaModelContext);

        @SuppressWarnings("hiding")
        class Order {
        }
        class Customer {
            @SuppressWarnings("unused")
            public Collection<Order> getOrders() {
                return null;
            }
        }

        class CustomerEx extends Customer {
        }

        final Method collectionAccessorMethod = findMethod(Customer.class, "getOrders");

        facetFactory.process(ProcessMethodContext
                .forTesting(CustomerEx.class, null, collectionAccessorMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(PropertyOrCollectionAccessorFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof CollectionAccessorFacetViaAccessor);
        final CollectionAccessorFacetViaAccessor collectionAccessorFacetViaMethod = (CollectionAccessorFacetViaAccessor) facet;
        assertEquals(collectionAccessorMethod, collectionAccessorFacetViaMethod.getMethods().getFirstOrFail());
    }

    static class Order {
    }


}
