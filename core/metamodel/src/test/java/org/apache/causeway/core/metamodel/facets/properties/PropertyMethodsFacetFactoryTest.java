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
package org.apache.causeway.core.metamodel.facets.properties;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.causeway.core.metamodel.facetapi.Facet;
import org.apache.causeway.core.metamodel.facetapi.FeatureType;
import org.apache.causeway.core.metamodel.facets.AbstractFacetFactoryTest;
import org.apache.causeway.core.metamodel.facets.FacetFactory.ProcessMethodContext;
import org.apache.causeway.core.metamodel.facets.members.disabled.method.DisableForContextFacet;
import org.apache.causeway.core.metamodel.facets.members.disabled.method.DisableForContextFacetViaMethod;
import org.apache.causeway.core.metamodel.facets.members.disabled.method.DisableForContextFacetViaMethodFactory;
import org.apache.causeway.core.metamodel.facets.members.hidden.method.HideForContextFacet;
import org.apache.causeway.core.metamodel.facets.members.hidden.method.HideForContextFacetViaMethod;
import org.apache.causeway.core.metamodel.facets.members.hidden.method.HideForContextFacetViaMethodFactory;
import org.apache.causeway.core.metamodel.facets.propcoll.accessor.PropertyOrCollectionAccessorFacet;
import org.apache.causeway.core.metamodel.facets.propcoll.memserexcl.SnapshotExcludeFacet;
import org.apache.causeway.core.metamodel.facets.properties.accessor.PropertyAccessorFacetViaAccessor;
import org.apache.causeway.core.metamodel.facets.properties.accessor.PropertyAccessorFacetViaAccessorFactory;
import org.apache.causeway.core.metamodel.facets.properties.autocomplete.PropertyAutoCompleteFacet;
import org.apache.causeway.core.metamodel.facets.properties.autocomplete.method.PropertyAutoCompleteFacetMethod;
import org.apache.causeway.core.metamodel.facets.properties.autocomplete.method.PropertyAutoCompleteFacetMethodFactory;
import org.apache.causeway.core.metamodel.facets.properties.choices.PropertyChoicesFacet;
import org.apache.causeway.core.metamodel.facets.properties.choices.method.PropertyChoicesFacetViaMethod;
import org.apache.causeway.core.metamodel.facets.properties.choices.method.PropertyChoicesFacetViaMethodFactory;
import org.apache.causeway.core.metamodel.facets.properties.defaults.PropertyDefaultFacet;
import org.apache.causeway.core.metamodel.facets.properties.defaults.method.PropertyDefaultFacetViaMethod;
import org.apache.causeway.core.metamodel.facets.properties.defaults.method.PropertyDefaultFacetViaMethodFactory;
import org.apache.causeway.core.metamodel.facets.properties.update.PropertySetterFacetFactory;
import org.apache.causeway.core.metamodel.facets.properties.update.clear.PropertyClearFacet;
import org.apache.causeway.core.metamodel.facets.properties.update.clear.PropertyClearFacetViaSetterMethod;
import org.apache.causeway.core.metamodel.facets.properties.update.init.PropertyInitializationFacet;
import org.apache.causeway.core.metamodel.facets.properties.update.init.PropertyInitializationFacetViaSetterMethod;
import org.apache.causeway.core.metamodel.facets.properties.update.modify.PropertySetterFacet;
import org.apache.causeway.core.metamodel.facets.properties.update.modify.PropertySetterFacetViaSetterMethod;
import org.apache.causeway.core.metamodel.facets.properties.validating.PropertyValidateFacet;
import org.apache.causeway.core.metamodel.facets.properties.validating.method.PropertyValidateFacetViaMethod;
import org.apache.causeway.core.metamodel.facets.properties.validating.method.PropertyValidateFacetViaMethodFactory;

import lombok.val;

class PropertyMethodsFacetFactoryTest
extends AbstractFacetFactoryTest {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testPropertyAccessorFacetIsInstalledAndMethodRemoved() {
        val facetFactory = new PropertyAccessorFacetViaAccessorFactory(metaModelContext);

        class Customer {
            @SuppressWarnings("unused")
            public String getFirstName() {
                return null;
            }
        }
        final Method propertyAccessorMethod = findMethod(Customer.class, "getFirstName");

        facetFactory.process(ProcessMethodContext
                .forTesting(Customer.class, null, propertyAccessorMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(PropertyOrCollectionAccessorFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof PropertyAccessorFacetViaAccessor);
        final PropertyAccessorFacetViaAccessor propertyAccessorFacetViaAccessor = (PropertyAccessorFacetViaAccessor) facet;
        assertEquals(propertyAccessorMethod, propertyAccessorFacetViaAccessor.getMethods().getFirstOrFail());

        assertTrue(methodRemover.getRemovedMethodMethodCalls().contains(propertyAccessorMethod));
    }

    public void testSetterFacetIsInstalledForSetterMethodAndMethodRemoved() {
        val facetFactory = new PropertySetterFacetFactory(metaModelContext);

        class Customer {
            @SuppressWarnings("unused")
            public String getFirstName() {
                return null;
            }

            @SuppressWarnings("unused")
            public void setFirstName(final String firstName) {
            }
        }
        final Method propertyAccessorMethod = findMethod(Customer.class, "getFirstName");
        final Method propertySetterMethod = findMethod(Customer.class, "setFirstName", new Class[] { String.class });

        facetFactory.process(ProcessMethodContext
                .forTesting(Customer.class, null, propertyAccessorMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(PropertySetterFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof PropertySetterFacetViaSetterMethod);
        final PropertySetterFacetViaSetterMethod propertySetterFacet = (PropertySetterFacetViaSetterMethod) facet;
        assertEquals(propertySetterMethod, propertySetterFacet.getMethods().getFirstOrFail());

        assertTrue(methodRemover.getRemovedMethodMethodCalls().contains(propertySetterMethod));
    }

    public void testInitializationFacetIsInstalledForSetterMethodAndMethodRemoved() {
        val facetFactory = new PropertySetterFacetFactory(metaModelContext);

        class Customer {
            @SuppressWarnings("unused")
            public String getFirstName() {
                return null;
            }

            @SuppressWarnings("unused")
            public void setFirstName(final String firstName) {
            }
        }
        final Method propertyAccessorMethod = findMethod(Customer.class, "getFirstName");
        final Method propertySetterMethod = findMethod(Customer.class, "setFirstName", new Class[] { String.class });

        facetFactory.process(ProcessMethodContext
                .forTesting(Customer.class, null, propertyAccessorMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(PropertyInitializationFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof PropertyInitializationFacet);
        final PropertyInitializationFacetViaSetterMethod propertySetterFacet = (PropertyInitializationFacetViaSetterMethod) facet;
        assertEquals(propertySetterMethod, propertySetterFacet.getMethods().getFirstOrFail());

        assertTrue(methodRemover.getRemovedMethodMethodCalls().contains(propertySetterMethod));
    }

    public void testSetterFacetIsInstalledMeansNoDisabledOrDerivedFacetsInstalled() {
        val facetFactory = new PropertySetterFacetFactory(metaModelContext);

        class Customer {
            @SuppressWarnings("unused")
            public String getFirstName() {
                return null;
            }

            @SuppressWarnings("unused")
            public void setFirstName(final String firstName) {
            }
        }
        final Method propertyAccessorMethod = findMethod(Customer.class, "getFirstName");

        facetFactory.process(ProcessMethodContext
                .forTesting(Customer.class, null, propertyAccessorMethod, methodRemover, facetedMethod));

        assertNull(facetedMethod.getFacet(SnapshotExcludeFacet.class));
        assertNull(facetedMethod.getFacet(SnapshotExcludeFacet.class));
    }

    public void testClearFacetViaSetterIfNoExplicitClearMethod() {
        val facetFactory = new PropertySetterFacetFactory(metaModelContext);

        class Customer {
            @SuppressWarnings("unused")
            public String getFirstName() {
                return null;
            }

            @SuppressWarnings("unused")
            public void setFirstName(final String firstName) {
            }
        }
        final Method propertyAccessorMethod = findMethod(Customer.class, "getFirstName");
        final Method propertySetterMethod = findMethod(Customer.class, "setFirstName", new Class[] { String.class });

        facetFactory.process(ProcessMethodContext
                .forTesting(Customer.class, null, propertyAccessorMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(PropertyClearFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof PropertyClearFacetViaSetterMethod);
        final PropertyClearFacetViaSetterMethod propertyClearFacet = (PropertyClearFacetViaSetterMethod) facet;
        assertEquals(propertySetterMethod, propertyClearFacet.getMethods().getFirstOrFail());
    }

    public void testChoicesFacetFoundAndMethodRemoved() {
        val facetFactory = new PropertyChoicesFacetViaMethodFactory(metaModelContext);

        class Customer {
            @SuppressWarnings("unused")
            public String getFirstName() {
                return null;
            }

            @SuppressWarnings("unused")
            public String[] choicesFirstName() {
                return null;
            }
        }
        final Method propertyAccessorMethod = findMethod(Customer.class, "getFirstName");
        final Method propertyChoicesMethod = findMethod(Customer.class, "choicesFirstName");

        facetFactory.process(ProcessMethodContext
                .forTesting(Customer.class, FeatureType.PROPERTY, propertyAccessorMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(PropertyChoicesFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof PropertyChoicesFacetViaMethod);
        final PropertyChoicesFacetViaMethod propertyChoicesFacet = (PropertyChoicesFacetViaMethod) facet;
        assertEquals(propertyChoicesMethod, propertyChoicesFacet.getMethods().getFirstOrFail());

        assertTrue(methodRemover.getRemovedMethodMethodCalls().contains(propertyChoicesMethod));
    }

    public void testAutoCompleteFacetFoundAndMethodRemoved() {

        val facetFactory = new PropertyAutoCompleteFacetMethodFactory(metaModelContext);

        class Customer {
            @SuppressWarnings("unused")
            public String getFirstName() {
                return null;
            }

            @SuppressWarnings("unused")
            public String[] autoCompleteFirstName(final String searchArg) {
                return null;
            }
        }
        final Method propertyAccessorMethod = findMethod(Customer.class, "getFirstName");
        final Method propertyAutoCompleteMethod = findMethod(Customer.class, "autoCompleteFirstName", new Class[]{String.class});

        facetFactory.process(ProcessMethodContext
                .forTesting(Customer.class, FeatureType.PROPERTY, propertyAccessorMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(PropertyAutoCompleteFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof PropertyAutoCompleteFacetMethod);
        final PropertyAutoCompleteFacetMethod propertyAutoCompleteFacet = (PropertyAutoCompleteFacetMethod) facet;
        assertEquals(propertyAutoCompleteMethod, propertyAutoCompleteFacet.getMethods().getFirstOrFail());

        assertTrue(methodRemover.getRemovedMethodMethodCalls().contains(propertyAutoCompleteMethod));
    }

    public void testDefaultFacetFoundAndMethodRemoved() {
        val facetFactory = new PropertyDefaultFacetViaMethodFactory(metaModelContext);

        class Customer {
            @SuppressWarnings("unused")
            public String getFirstName() {
                return null;
            }

            @SuppressWarnings("unused")
            public String defaultFirstName() {
                return null;
            }
        }
        final Method propertyAccessorMethod = findMethod(Customer.class, "getFirstName");
        final Method propertyDefaultMethod = findMethod(Customer.class, "defaultFirstName");

        facetFactory.process(ProcessMethodContext
                .forTesting(Customer.class, FeatureType.PROPERTY, propertyAccessorMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(PropertyDefaultFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof PropertyDefaultFacetViaMethod);
        final PropertyDefaultFacetViaMethod propertyDefaultFacet = (PropertyDefaultFacetViaMethod) facet;
        assertEquals(propertyDefaultMethod, propertyDefaultFacet.getMethods().getFirstOrFail());

        assertTrue(methodRemover.getRemovedMethodMethodCalls().contains(propertyDefaultMethod));
    }

    public void testValidateFacetFoundAndMethodRemoved() {
        val facetFactory = new PropertyValidateFacetViaMethodFactory(metaModelContext);

        class Customer {
            @SuppressWarnings("unused")
            public String getFirstName() {
                return null;
            }

            @SuppressWarnings("unused")
            public String validateFirstName(final String firstName) {
                return null;
            }
        }
        final Method propertyAccessorMethod = findMethod(Customer.class, "getFirstName");
        final Method propertyValidateMethod = findMethod(Customer.class, "validateFirstName", new Class[] { String.class });

        facetFactory.process(ProcessMethodContext
                .forTesting(Customer.class, FeatureType.PROPERTY, propertyAccessorMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(PropertyValidateFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof PropertyValidateFacetViaMethod);
        final PropertyValidateFacetViaMethod propertyValidateFacet = (PropertyValidateFacetViaMethod) facet;
        assertEquals(propertyValidateMethod, propertyValidateFacet.getMethods().getFirstOrFail());

        assertTrue(methodRemover.getRemovedMethodMethodCalls().contains(propertyValidateMethod));
    }

    public void testDisableFacetFoundAndMethodRemoved() {
        val facetFactory = new DisableForContextFacetViaMethodFactory(metaModelContext);

        class Customer {
            @SuppressWarnings("unused")
            public String getFirstName() {
                return null;
            }

            @SuppressWarnings("unused")
            public String disableFirstName() {
                return "disabled";
            }
        }
        final Method propertyAccessorMethod = findMethod(Customer.class, "getFirstName");
        final Method propertyDisableMethod = findMethod(Customer.class, "disableFirstName", new Class[] {});

        facetFactory.process(ProcessMethodContext
                .forTesting(Customer.class, FeatureType.PROPERTY, propertyAccessorMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(DisableForContextFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof DisableForContextFacetViaMethod);
        final DisableForContextFacetViaMethod disableForContextFacet = (DisableForContextFacetViaMethod) facet;
        assertEquals(propertyDisableMethod, disableForContextFacet.getMethods().getFirstOrFail());

        assertTrue(methodRemover.getRemovedMethodMethodCalls().contains(propertyDisableMethod));
    }

    public void testDisableFacetNoArgsFoundAndMethodRemoved() {

        val facetFactory = new DisableForContextFacetViaMethodFactory(metaModelContext);

        class Customer {
            @SuppressWarnings("unused")
            public String getFirstName() {
                return null;
            }

            @SuppressWarnings("unused")
            public String disableFirstName() {
                return "disabled";
            }
        }
        final Method propertyAccessorMethod = findMethod(Customer.class, "getFirstName");
        final Method propertyDisableMethod = findMethod(Customer.class, "disableFirstName");

        facetFactory.process(ProcessMethodContext
                .forTesting(Customer.class, FeatureType.PROPERTY, propertyAccessorMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(DisableForContextFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof DisableForContextFacetViaMethod);
        final DisableForContextFacetViaMethod disableForContextFacet = (DisableForContextFacetViaMethod) facet;
        assertEquals(propertyDisableMethod, disableForContextFacet.getMethods().getFirstOrFail());

        assertTrue(methodRemover.getRemovedMethodMethodCalls().contains(propertyDisableMethod));
    }

    public void testHiddenFacetFoundAndMethodRemoved() {
        val facetFactory = new HideForContextFacetViaMethodFactory(metaModelContext);

        class Customer {
            @SuppressWarnings("unused")
            public String getFirstName() {
                return null;
            }

            @SuppressWarnings("unused")
            public boolean hideFirstName() {
                return true;
            }
        }
        final Method propertyAccessorMethod = findMethod(Customer.class, "getFirstName");
        final Method propertyHideMethod = findMethod(Customer.class, "hideFirstName", new Class[] {});

        facetFactory.process(ProcessMethodContext
                .forTesting(Customer.class, FeatureType.PROPERTY, propertyAccessorMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(HideForContextFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof HideForContextFacetViaMethod);
        final HideForContextFacetViaMethod hideForContextFacet = (HideForContextFacetViaMethod) facet;
        assertEquals(propertyHideMethod, hideForContextFacet.getMethods().getFirstOrFail());

        assertTrue(methodRemover.getRemovedMethodMethodCalls().contains(propertyHideMethod));
    }

    public void testHiddenFacetWithNoArgFoundAndMethodRemoved() {
        val facetFactory = new HideForContextFacetViaMethodFactory(metaModelContext);

        class Customer {
            @SuppressWarnings("unused")
            public String getFirstName() {
                return null;
            }

            @SuppressWarnings("unused")
            public boolean hideFirstName() {
                return true;
            }
        }
        final Method propertyAccessorMethod = findMethod(Customer.class, "getFirstName");
        final Method propertyHideMethod = findMethod(Customer.class, "hideFirstName");

        facetFactory.process(ProcessMethodContext
                .forTesting(Customer.class, FeatureType.PROPERTY, propertyAccessorMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(HideForContextFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof HideForContextFacetViaMethod);
        final HideForContextFacetViaMethod hideForContextFacet = (HideForContextFacetViaMethod) facet;
        assertEquals(propertyHideMethod, hideForContextFacet.getMethods().getFirstOrFail());

        assertTrue(methodRemover.getRemovedMethodMethodCalls().contains(propertyHideMethod));
    }

    public void testPropertyFoundOnSuperclass() {
        val facetFactory = new PropertyAccessorFacetViaAccessorFactory(metaModelContext);

        class Customer {
            @SuppressWarnings("unused")
            public String getFirstName() {
                return null;
            }
        }

        class CustomerEx extends Customer {
        }

        final Method propertyAccessorMethod = findMethod(Customer.class, "getFirstName");

        facetFactory.process(ProcessMethodContext
                .forTesting(CustomerEx.class, null, propertyAccessorMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(PropertyOrCollectionAccessorFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof PropertyAccessorFacetViaAccessor);
        final PropertyAccessorFacetViaAccessor accessorFacet = (PropertyAccessorFacetViaAccessor) facet;
        assertEquals(propertyAccessorMethod, accessorFacet.getMethods().getFirstOrFail());
    }

    public void testPropertyFoundOnSuperclassButHelperMethodFoundOnSubclass() {
        val facetFactory = new PropertyAccessorFacetViaAccessorFactory(metaModelContext);
        val facetFactoryForHide = new HideForContextFacetViaMethodFactory(metaModelContext);
        val facetFactoryForDisable = new DisableForContextFacetViaMethodFactory(metaModelContext);

        class Customer {
            @SuppressWarnings("unused")
            public String getFirstName() {
                return null;
            }
        }

        class CustomerEx extends Customer {
            @SuppressWarnings("unused")
            public boolean hideFirstName() {
                return true;
            }

            @SuppressWarnings("unused")
            public String disableFirstName() {
                return "disabled";
            }
        }

        final Method propertyAccessorMethod = findMethod(Customer.class, "getFirstName");
        final Method propertyHideMethod = findMethod(CustomerEx.class, "hideFirstName");
        final Method propertyDisableMethod = findMethod(CustomerEx.class, "disableFirstName");

        final ProcessMethodContext processMethodContext = ProcessMethodContext
                .forTesting(CustomerEx.class, FeatureType.PROPERTY,
                propertyAccessorMethod, methodRemover, facetedMethod);
        facetFactory.process(processMethodContext);
        facetFactoryForHide.process(processMethodContext);
        facetFactoryForDisable.process(processMethodContext);

        final Facet facet = facetedMethod.getFacet(HideForContextFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof HideForContextFacetViaMethod);
        final HideForContextFacetViaMethod hideForContextFacet = (HideForContextFacetViaMethod) facet;
        assertEquals(propertyHideMethod, hideForContextFacet.getMethods().getFirstOrFail());

        final Facet facet2 = facetedMethod.getFacet(DisableForContextFacet.class);
        assertNotNull(facet2);
        assertTrue(facet2 instanceof DisableForContextFacetViaMethod);
        final DisableForContextFacetViaMethod disableForContextFacet = (DisableForContextFacetViaMethod) facet2;
        assertEquals(propertyDisableMethod, disableForContextFacet.getMethods().getFirstOrFail());
    }




}
