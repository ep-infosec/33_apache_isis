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
package org.apache.causeway.core.metamodel.spec.feature;

import java.util.Optional;
import java.util.function.Predicate;

import org.springframework.lang.Nullable;

import org.apache.causeway.applib.annotation.Domain;
import org.apache.causeway.commons.collections.Can;
import org.apache.causeway.core.metamodel.consent.Consent;
import org.apache.causeway.core.metamodel.consent.InteractionInitiatedBy;
import org.apache.causeway.core.metamodel.facetapi.FeatureType;
import org.apache.causeway.core.metamodel.facets.all.named.MemberNamedFacet;
import org.apache.causeway.core.metamodel.interactions.ActionArgValidityContext;
import org.apache.causeway.core.metamodel.interactions.InteractionHead;
import org.apache.causeway.core.metamodel.interactions.managed.ParameterNegotiationModel;
import org.apache.causeway.core.metamodel.object.ManagedObject;
import org.apache.causeway.core.metamodel.spec.ObjectSpecification;
import org.apache.causeway.core.metamodel.spec.feature.memento.ActionParameterMemento;
import org.apache.causeway.core.metamodel.util.Facets;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.experimental.UtilityClass;

/**
 * Analogous to {@link ObjectAssociation}.
 */
public interface ObjectActionParameter
extends ObjectFeature, CurrentHolder {

    /**
     * Owning {@link ObjectAction}.
     */
    ObjectAction getAction();

    /**
     * Returns the 0-based index to this parameter.
     */
    int getParameterIndex();

    default boolean isSingular() {
        return getFeatureType()==FeatureType.ACTION_PARAMETER_SINGULAR;
    }

    default boolean isPlural() {
        return getFeatureType()==FeatureType.ACTION_PARAMETER_PLURAL;
    }

    /**
     * Returns the name of this parameter.
     *
     * <p>
     * Because Java's reflection API does not allow us to access the code name
     * of the parameter, we have to do figure out the name of the parameter
     * ourselves:
     * <ul>
     * <li>If there is a {@link MemberNamedFacet} associated with this parameter then
     * we infer a name from this, eg "First Name" becomes "firstName".
     * <li>Otherwise we use the type, eg "string".
     * <li>If there is more than one parameter of the same type, then we use a
     * numeric suffix (eg "string1", "string2"). Wrappers and primitives are
     * considered to be the same type.
     * </ul>
     */
    @Override
    Optional<String> getStaticFriendlyName();

    // internal API
    ActionArgValidityContext createProposedArgumentInteractionContext(
            InteractionHead head,
            Can<ManagedObject> args,
            int position,
            InteractionInitiatedBy interactionInitiatedBy);


    /**
     * Whether there is an autoComplete provided (eg <tt>autoCompleteXxx</tt> supporting
     * method) for the parameter.
     */
    boolean hasAutoComplete();

    /**
     * Returns a list of possible references/values for this parameter, which the
     * user can choose from, based on the input search argument.
     */
    Can<ManagedObject> getAutoComplete(
            ParameterNegotiationModel pendingArgs,
            String searchArg,
            InteractionInitiatedBy interactionInitiatedBy);



    int getAutoCompleteMinLength();
    /**
     * Whether there are any choices provided (eg <tt>choicesXxx</tt> supporting
     * method) for the parameter.
     */
    boolean hasChoices();

    /**
     * Returns a list of possible references/values for this parameter, which the
     * user can choose from.
     */
    Can<ManagedObject> getChoices(
            ParameterNegotiationModel pendingArgs,
            InteractionInitiatedBy interactionInitiatedBy);

    /**
     * Needs to account for 2 scenarios,
     * <ul>
     * <li>there is no default providing facet associated with this parameter,
     * we return the corresponding value from the given {@link ParameterNegotiationModel}</li>
     * <li>there is a default providing facet associated with this parameter,
     * it may return {@code null}, but in any case the value is wrapped in a
     * non-null {@link ManagedObject}</li>
     * </ul>
     * @return a {@link ManagedObject}, {@code null} is represented by an empty
     * but non-null {@link ManagedObject}
     */
    @NonNull ManagedObject getDefault(ParameterNegotiationModel pendingArgs);

    /**
     * Reassesses the current parameter value, that is applying <i>defaults semantics</i>,
     * whenever a parameter this one depends on changes in the UI. Parameters
     * with higher index depend on those with lower index.
     * <p>
     * Reassessment can be switch off by means of {@link org.apache.causeway.applib.annotation.Parameter#dependentDefaultsPolicy()}.
     */
    default void reassessDefault(final ParameterNegotiationModel pendingArgs) {
        val paramIndex = getParameterIndex();
        val bindableParamDirtyFlag = pendingArgs.getBindableParamValueDirtyFlag(paramIndex);
        if(Facets.dependentDefaultsPolicy(this).isUpdateDependent()
                // always allow when not dirtied by the user (UI)
                || ! bindableParamDirtyFlag.getValue().booleanValue() ) {
            // reassess defaults honoring defaults semantics
            val paramDefaultValue = this.getDefault(pendingArgs);
            pendingArgs.setParamValue(paramIndex, paramDefaultValue);
            bindableParamDirtyFlag.setValue(false); // clear dirty flag
        }
    }

    @NonNull default ManagedObject getEmpty() {
        return ManagedObject.empty(getElementType());
    }

    /**
     * Whether this parameter is visible given the entered previous arguments
     * @param head
     * @param pendingArgs
     * @param interactionInitiatedBy
     */
    Consent isVisible(
            InteractionHead head,
            Can<ManagedObject> pendingArgs,
            InteractionInitiatedBy interactionInitiatedBy);

    /**
     * Whether this parameter is disabled given the entered previous arguments
     * @param head
     * @param pendingArgs
     * @param interactionInitiatedBy
     */
    Consent isUsable(
            InteractionHead head,
            Can<ManagedObject> pendingArgs,
            InteractionInitiatedBy interactionInitiatedBy);

    /**
     * Whether proposed value for this parameter is valid.
     *
     * @param head
     * @param pendingArgs
     * @param interactionInitiatedBy
     */
    Consent isValid(
            InteractionHead head,
            Can<ManagedObject> pendingArgs,
            InteractionInitiatedBy interactionInitiatedBy);

    @Domain.Exclude
    @UtilityClass
    public static class Predicates {

        /**
         * A predicate for action parameters that checks that the parameter collection element type
         * is the same as assignable (that is, the same or a supertype of, supporting co-variance) the
         * {@link ObjectSpecification element type} provided in the constructor.
         * <p>
         * For example, a parented collection of <tt>LeaseTerm</tt>s provides an elementSpec of <tt>LeaseTerm</tt>
         * into the constructor.  An action with signature <tt>removeTerms(List&lt;LeaseTerm>)</tt> would match on
         * its (first) parameter.
         * <p>
         * For example, a parented collection of <tt>LeaseTermForServiceCharge</tt>s provides an elementSpec
         * of <tt>LeaseTermForServiceCharge</tt> into the constructor.  An action with signature
         * <tt>removeTerms(List&lt;LeaseTerm>)</tt> would match on its (first) parameter.
         */
        @RequiredArgsConstructor
        public class CollectionParameter implements Predicate<ObjectActionParameter> {

            private final @NonNull ObjectSpecification elementType;

            @Override
            public boolean test(final @Nullable ObjectActionParameter objectActionParameter) {
                return objectActionParameter instanceof OneToManyActionParameter
                        && elementType.isOfType(objectActionParameter.getElementType());
            }
        }

        /**
         * A predicate for action parameters that checks that the parameter collection element type
         * is exactly the same as the {@link ObjectSpecification element type} (no co/contra-variance)
         * provided in the constructor.
         * <p>
         * For example, a parented collection of <tt>LeaseTerm</tt>s provides an elementSpec of <tt>LeaseTerm</tt>
         * into the constructor.  An action with signature <tt>addTerm(LeaseTerm)</tt> would match on
         * its (first) parameter.
         */
        @RequiredArgsConstructor
        public class ScalarParameter implements Predicate<ObjectActionParameter> {

            private final @NonNull ObjectSpecification elementType;

            @Override
            public boolean test(final @Nullable ObjectActionParameter objectActionParameter) {
                return objectActionParameter instanceof OneToOneActionParameter
                        && elementType == objectActionParameter.getElementType();
            }
        }
    }

    default String getCssClass(final String prefix) {
        return getAction().getCssClass(prefix) + "-" + getId();
    }

    /**
     * Returns a serializable representation of this parameter.
     */
    default ActionParameterMemento getMemento() {
        return ActionParameterMemento.forActionParameter(this);
    }



}
