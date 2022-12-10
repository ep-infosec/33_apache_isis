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
package org.apache.causeway.core.metamodel.facets.actions.action;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.causeway.applib.annotation.Action;
import org.apache.causeway.applib.annotation.Publishing;
import org.apache.causeway.core.metamodel.facetapi.Facet;
import org.apache.causeway.core.metamodel.facets.FacetFactory.ProcessMethodContext;
import org.apache.causeway.core.metamodel.facets.members.publish.command.CommandPublishingFacet;
import org.apache.causeway.core.metamodel.facets.members.publish.command.CommandPublishingFacetForActionAnnotation;

import lombok.val;

class ActionAnnotationFacetFactoryTest_commandPublishing
extends ActionAnnotationFacetFactoryTest {

    private void processCommandPublishing(
            final ActionAnnotationFacetFactory facetFactory, final ProcessMethodContext processMethodContext) {
        val actionIfAny = processMethodContext.synthesizeOnMethod(Action.class);
        facetFactory.processCommandPublishing(processMethodContext, actionIfAny);
    }

    @Test
    void given_HasInteractionId_thenIgnored() {
        // given
        final Method actionMethod = findMethod(SomeHasInteractionId.class, "someAction");

        // when
        processCommandPublishing(facetFactory, ProcessMethodContext
                .forTesting(SomeHasInteractionId.class, null, actionMethod, mockMethodRemover, facetedMethod));

        // then
        assertFalse(CommandPublishingFacet.isPublishingEnabled(facetedMethod));
    }

    @Test
    void given_annotation_but_command_not_specified_then_facet_not_added() {

        // given
        class Customer {
            @Action()
            public void someAction() {
            }
        }
        final Method actionMethod = findMethod(Customer.class, "someAction");

        // when
        processCommandPublishing(facetFactory, ProcessMethodContext
                .forTesting(Customer.class, null, actionMethod, mockMethodRemover, facetedMethod));

        // then
        assertFalse(CommandPublishingFacet.isPublishingEnabled(facetedMethod));
    }

    @Test
    void given_annotation_with_command_enabled_then_facet_added() {

        // given
        class Customer {
            @Action(commandPublishing = Publishing.ENABLED)
            public void someAction() {
            }
        }
        final Method actionMethod = findMethod(Customer.class, "someAction");

        // when
        processCommandPublishing(facetFactory, ProcessMethodContext
                .forTesting(Customer.class, null, actionMethod, mockMethodRemover, facetedMethod));

        // then
        final Facet facet = facetedMethod.getFacet(CommandPublishingFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof CommandPublishingFacetForActionAnnotation);
    }


    @Test
    void given_annotation_with_command_disabled_then_facet_not_added() {

        // given
        class Customer {
            @Action(commandPublishing = Publishing.DISABLED)
            public void someAction() {
            }
        }
        final Method actionMethod = findMethod(Customer.class, "someAction");

        // when
        processCommandPublishing(facetFactory, ProcessMethodContext
                .forTesting(Customer.class, null, actionMethod, mockMethodRemover, facetedMethod));

        // then
        assertFalse(CommandPublishingFacet.isPublishingEnabled(facetedMethod));
    }

}
