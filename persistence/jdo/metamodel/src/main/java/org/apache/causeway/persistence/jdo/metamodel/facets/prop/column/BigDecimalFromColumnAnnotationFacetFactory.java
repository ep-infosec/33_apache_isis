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
package org.apache.causeway.persistence.jdo.metamodel.facets.prop.column;

import java.math.BigDecimal;

import javax.inject.Inject;
import javax.jdo.annotations.IdentityType;

import org.apache.causeway.core.metamodel.context.MetaModelContext;
import org.apache.causeway.core.metamodel.facetapi.FeatureType;
import org.apache.causeway.core.metamodel.facetapi.MetaModelRefiner;
import org.apache.causeway.core.metamodel.facets.FacetFactoryAbstract;
import org.apache.causeway.core.metamodel.facets.FacetedMethod;
import org.apache.causeway.core.metamodel.facets.objectvalue.digits.MaxFractionalDigitsFacet;
import org.apache.causeway.core.metamodel.facets.objectvalue.digits.MaxTotalDigitsFacet;
import org.apache.causeway.core.metamodel.progmodel.ProgrammingModel;
import org.apache.causeway.core.metamodel.spec.feature.MixedIn;
import org.apache.causeway.core.metamodel.spec.feature.ObjectAssociation;
import org.apache.causeway.core.metamodel.specloader.validator.ValidationFailure;
import org.apache.causeway.persistence.jdo.provider.metamodel.facets.object.persistencecapable.JdoPersistenceCapableFacet;
import org.apache.causeway.persistence.jdo.provider.metamodel.facets.prop.notpersistent.JdoNotPersistentFacet;

public class BigDecimalFromColumnAnnotationFacetFactory
extends FacetFactoryAbstract
implements MetaModelRefiner {

    @Inject
    public BigDecimalFromColumnAnnotationFacetFactory(final MetaModelContext mmc) {
        super(mmc, FeatureType.PROPERTIES_ONLY);
    }

    @Override
    public void process(final ProcessMethodContext processMethodContext) {

        if(BigDecimal.class != processMethodContext.getMethod().getReturnType()) {
            return;
        }

        final FacetedMethod holder = processMethodContext.getFacetHolder();

        _ColumnUtil.processColumnAnnotations(processMethodContext,
                jdoColumnIfAny->{
                    addFacetIfPresent(
                            MaxTotalDigitsFacetFromJdoColumnAnnotation
                            .createJdo(jdoColumnIfAny, holder));

                    addFacetIfPresent(
                            MaxFractionalDigitsFacetFromJdoColumn
                            .createJdo(jdoColumnIfAny, holder));
                },
                jpaColumnIfAny->{
                    addFacetIfPresent(
                            MaxTotalDigitsFacetFromJdoColumnAnnotation
                            .createJpa(jpaColumnIfAny, holder));

                    addFacetIfPresent(
                            MaxFractionalDigitsFacetFromJdoColumn
                            .createJpa(jpaColumnIfAny, holder));
                });

    }

    @Override
    public void refineProgrammingModel(final ProgrammingModel programmingModel) {
        programmingModel.addVisitingValidatorSkipManagedBeans(spec->{

            // only consider persistent entities
            final JdoPersistenceCapableFacet pcFacet = spec.getFacet(JdoPersistenceCapableFacet.class);
            if(pcFacet==null || pcFacet.getIdentityType() == IdentityType.NONDURABLE) {
                return;
            }

            spec.streamProperties(MixedIn.EXCLUDED)
            // skip checks if annotated with JDO @NotPersistent
            .filter(association->!association.containsNonFallbackFacet(JdoNotPersistentFacet.class))
            .forEach(association->{
                validateBigDecimalValueFacet(association);
            });

        });
    }

    private static void validateBigDecimalValueFacet(final ObjectAssociation association) {

        association.lookupFacet(MaxTotalDigitsFacet.class)
        .map(MaxTotalDigitsFacet::getSharedFacetRankingElseFail)
        .ifPresent(facetRanking->facetRanking
                .visitTopRankPairsSemanticDiffering(MaxTotalDigitsFacet.class, (a, b)->{

                    ValidationFailure.raiseFormatted(
                            association,
                            "%s: inconsistent MaxTotalDigits semantics specified in %s and %s.",
                            association.getFeatureIdentifier().toString(),
                            a.getClass().getSimpleName(),
                            b.getClass().getSimpleName());
                }));

        association.lookupFacet(MaxFractionalDigitsFacet.class)
        .map(MaxFractionalDigitsFacet::getSharedFacetRankingElseFail)
        .ifPresent(facetRanking->facetRanking
                .visitTopRankPairsSemanticDiffering(MaxFractionalDigitsFacet.class, (a, b)->{

                    ValidationFailure.raiseFormatted(
                            association,
                            "%s: inconsistent MaxFractionalDigits semantics specified in %s and %s.",
                            association.getFeatureIdentifier().toString(),
                            a.getClass().getSimpleName(),
                            b.getClass().getSimpleName());
                }));

    }


}
