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
package org.apache.causeway.core.metamodel.services.grid.bootstrap;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import org.apache.causeway.applib.annotation.ActionLayout;
import org.apache.causeway.applib.annotation.PriorityPrecedence;
import org.apache.causeway.applib.layout.LayoutConstants;
import org.apache.causeway.applib.layout.component.ActionLayoutData;
import org.apache.causeway.applib.layout.component.ActionLayoutDataOwner;
import org.apache.causeway.applib.layout.component.CollectionLayoutData;
import org.apache.causeway.applib.layout.component.DomainObjectLayoutData;
import org.apache.causeway.applib.layout.component.FieldSet;
import org.apache.causeway.applib.layout.component.PropertyLayoutData;
import org.apache.causeway.applib.layout.grid.Grid;
import org.apache.causeway.applib.layout.grid.bootstrap.BSCol;
import org.apache.causeway.applib.layout.grid.bootstrap.BSGrid;
import org.apache.causeway.applib.layout.grid.bootstrap.BSRow;
import org.apache.causeway.applib.layout.grid.bootstrap.BSTab;
import org.apache.causeway.applib.layout.grid.bootstrap.BSTabGroup;
import org.apache.causeway.applib.layout.grid.bootstrap.Size;
import org.apache.causeway.applib.services.grid.GridMarshallerService;
import org.apache.causeway.applib.services.i18n.TranslationService;
import org.apache.causeway.applib.services.jaxb.JaxbService;
import org.apache.causeway.applib.services.message.MessageService;
import org.apache.causeway.applib.value.NamedWithMimeType.CommonMimeType;
import org.apache.causeway.commons.internal.base._NullSafe;
import org.apache.causeway.commons.internal.collections._Lists;
import org.apache.causeway.commons.internal.collections._Maps;
import org.apache.causeway.commons.internal.collections._Sets;
import org.apache.causeway.commons.internal.exceptions._Exceptions;
import org.apache.causeway.commons.internal.resources._Resources;
import org.apache.causeway.core.config.environment.CausewaySystemEnvironment;
import org.apache.causeway.core.metamodel.CausewayModuleCoreMetamodel;
import org.apache.causeway.core.metamodel.facets.actions.position.ActionPositionFacet;
import org.apache.causeway.core.metamodel.facets.members.layout.group.GroupIdAndName;
import org.apache.causeway.core.metamodel.facets.members.layout.group.LayoutGroupFacet;
import org.apache.causeway.core.metamodel.layout.LayoutFacetUtil.LayoutDataFactory;
import org.apache.causeway.core.metamodel.services.grid.GridSystemServiceAbstract;
import org.apache.causeway.core.metamodel.spec.ObjectSpecification;
import org.apache.causeway.core.metamodel.spec.feature.MixedIn;
import org.apache.causeway.core.metamodel.spec.feature.ObjectAction;
import org.apache.causeway.core.metamodel.spec.feature.ObjectAssociation;
import org.apache.causeway.core.metamodel.spec.feature.ObjectMember;
import org.apache.causeway.core.metamodel.spec.feature.OneToManyAssociation;
import org.apache.causeway.core.metamodel.spec.feature.OneToOneAssociation;
import org.apache.causeway.core.metamodel.specloader.SpecificationLoader;

import static org.apache.causeway.commons.internal.base._NullSafe.stream;

import lombok.Setter;
import lombok.val;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;

@Service
@Named(CausewayModuleCoreMetamodel.NAMESPACE + ".GridSystemServiceBootstrap")
@Priority(PriorityPrecedence.MIDPOINT)
@Qualifier("Bootstrap")
@Log4j2
public class GridSystemServiceBootstrap
extends GridSystemServiceAbstract<BSGrid> {

    public static final String TNS = "http://causeway.apache.org/applib/layout/grid/bootstrap3";
    public static final String SCHEMA_LOCATION = "http://causeway.apache.org/applib/layout/grid/bootstrap3/bootstrap3.xsd";

    @Inject @Lazy // circular dependency (late binding)
    @Setter @Accessors(chain = true) // JUnit support
    private GridMarshallerService<BSGrid> marshaller;

    @Inject
    public GridSystemServiceBootstrap(
            final SpecificationLoader specificationLoader,
            final TranslationService translationService,
            final JaxbService jaxbService,
            final MessageService messageService,
            final CausewaySystemEnvironment causewaySystemEnvironment) {
        super(specificationLoader, translationService, jaxbService, messageService, causewaySystemEnvironment);
    }

    @Override
    public Class<BSGrid> gridImplementation() {
        return BSGrid.class;
    }

    @Override
    public String tns() {
        return TNS;
    }

    @Override
    public String schemaLocation() {
        return SCHEMA_LOCATION;
    }


    @Override
    public BSGrid defaultGrid(final Class<?> domainClass) {

        try {
            final String content = _Resources.loadAsStringUtf8(getClass(), "GridFallbackLayout.xml");
            return Optional.ofNullable(content)
                    .map(xml -> marshaller.unmarshal(xml, CommonMimeType.XML).getValue().orElse(null))
                    .filter(BSGrid.class::isInstance)
                    .map(BSGrid.class::cast)
                    .map(bsGrid -> withDomainClass(bsGrid, domainClass))
                    .orElseGet(() -> fallback(domainClass))
                    ;
        } catch (final Exception e) {
            return fallback(domainClass);
        }
    }

    //
    // only ever called if fail to load DefaultGrid.layout.xml,
    // which *really* shouldn't happen
    //
    private BSGrid fallback(final Class<?> domainClass) {
        final BSGrid bsGrid = withDomainClass(new BSGrid(), domainClass);

        final BSRow headerRow = new BSRow();
        bsGrid.getRows().add(headerRow);
        final BSCol headerRowCol = new BSCol();
        headerRowCol.setSpan(12);
        headerRowCol.setUnreferencedActions(true);
        headerRowCol.setDomainObject(new DomainObjectLayoutData());
        headerRow.getCols().add(headerRowCol);

        final BSRow propsRow = new BSRow();
        bsGrid.getRows().add(propsRow);

        // if no layout hints
        addFieldSetsToColumn(propsRow, 4, Arrays.asList("General"), true);

        final BSCol col = new BSCol();
        col.setUnreferencedCollections(true);
        col.setSpan(12);
        propsRow.getCols().add(col);

        return bsGrid;
    }

    private static BSGrid withDomainClass(final BSGrid bsGrid, final Class<?> domainClass) {
        bsGrid.setDomainClass(domainClass);
        return bsGrid;
    }

    static void addFieldSetsToColumn(
            final BSRow propsRow,
            final int span,
            final List<String> memberGroupNames,
            final boolean unreferencedProperties) {

        if(span > 0 || unreferencedProperties) {
            final BSCol col = new BSCol();
            col.setSpan(span); // in case we are here because of 'unreferencedProperties' needs setting
            propsRow.getCols().add(col);
            final List<String> leftMemberGroups = memberGroupNames;
            for (String memberGroup : leftMemberGroups) {
                final FieldSet fieldSet = new FieldSet();
                fieldSet.setName(memberGroup);
                // fieldSet's id will be derived from the name later
                // during normalization phase.
                if(unreferencedProperties && col.getFieldSets().isEmpty()) {
                    fieldSet.setUnreferencedProperties(true);
                }
                col.getFieldSets().add(fieldSet);
            }
        }
    }

    @Override
    protected boolean validateAndNormalize(
            final Grid grid,
            final Class<?> domainClass) {

        val bsGrid = (BSGrid) grid;
        val objectSpec = specificationLoader.specForTypeElseFail(domainClass);

        val oneToOneAssociationById = ObjectMember.mapById(objectSpec.streamProperties(MixedIn.INCLUDED));
        val oneToManyAssociationById = ObjectMember.mapById(objectSpec.streamCollections(MixedIn.INCLUDED));
        val objectActionById = ObjectMember.mapById(objectSpec.streamRuntimeActions(MixedIn.INCLUDED));

        val propertyLayoutDataById = bsGrid.getAllPropertiesById();
        val collectionLayoutDataById = bsGrid.getAllCollectionsById();
        val actionLayoutDataById = bsGrid.getAllActionsById();

        val gridModelIfValid = _GridModel.createFrom(bsGrid);
        if(!gridModelIfValid.isPresent()) { // only present if valid
            return false;
        }
        val gridModel = gridModelIfValid.get();

        val layoutDataFactory = LayoutDataFactory.of(objectSpec);

        // * surplus ... those defined in the grid model but not available with the meta-model
        // * missing ... those available with the meta-model but missing in the grid-model
        // (missing properties will be added to the first field-set of the specified column)

        val surplusAndMissingPropertyIds =
                surplusAndMissing(propertyLayoutDataById.keySet(),  oneToOneAssociationById.keySet());
        val surplusPropertyIds = surplusAndMissingPropertyIds.getSurplus();
        val missingPropertyIds = surplusAndMissingPropertyIds.getMissing();

        for (String surplusPropertyId : surplusPropertyIds) {
            propertyLayoutDataById.get(surplusPropertyId).setMetadataError("No such property");
        }

        // catalog which associations are bound to an existing field-set
        // so that (below) we can determine which missing property IDs are not unbound vs
        // which should be included in the field-set that they are bound to.
        val boundAssociationIdsByFieldSetId = _Maps.<String, Set<String>>newHashMap();

        for (val fieldSet : gridModel.fieldSets()) {
            val fieldSetId = GroupIdAndName.forFieldSet(fieldSet)
                .orElseThrow(()->_Exceptions.illegalArgument("invalid fieldSet detected, "
                        + "requires at least an id or a name"))
                .getId();
            Set<String> boundAssociationIds = boundAssociationIdsByFieldSetId.get(fieldSetId);
            if(boundAssociationIds == null) {
                boundAssociationIds = stream(fieldSet.getProperties())
                        .map(PropertyLayoutData::getId)
                        .collect(Collectors.toCollection(_Sets::newLinkedHashSet));
                boundAssociationIdsByFieldSetId.put(fieldSetId, boundAssociationIds);
            }
        }

        // 1-to-1-association IDs, that want to contribute to the 'metadata' FieldSet
        // but are unbound, because such a FieldSet is not defined by the given layout.
        val unboundMetadataContributingIds = _Sets.<String>newHashSet();

        // along with any specified by existing metadata
        for (final OneToOneAssociation oneToOneAssociation : oneToOneAssociationById.values()) {
            val layoutGroupFacet = oneToOneAssociation.getFacet(LayoutGroupFacet.class);
            if(layoutGroupFacet == null) {
                continue;
            }
            val id = layoutGroupFacet.getGroupId();
            if(gridModel.containsFieldSetId(id)) {
                Set<String> boundAssociationIds =
                        boundAssociationIdsByFieldSetId.computeIfAbsent(id, k -> _Sets.newLinkedHashSet());
                boundAssociationIds.add(oneToOneAssociation.getId());
            } else if(id.equals(LayoutConstants.FieldSetId.METADATA)) {
                unboundMetadataContributingIds.add(oneToOneAssociation.getId());
            }
        }

        if(!missingPropertyIds.isEmpty()) {

            val unboundPropertyIds = _Sets.newLinkedHashSet(missingPropertyIds);

            for (final String fieldSetId : boundAssociationIdsByFieldSetId.keySet()) {
                val boundPropertyIds = boundAssociationIdsByFieldSetId.get(fieldSetId);
                unboundPropertyIds.removeAll(boundPropertyIds);
            }

            for (final String fieldSetId : boundAssociationIdsByFieldSetId.keySet()) {
                val fieldSet = gridModel.getFieldSet(fieldSetId);
                val associationIds = boundAssociationIdsByFieldSetId.get(fieldSetId);

                val associations1To1Ids =
                        associationIds.stream()
                        .map(oneToOneAssociationById::get)
                        .filter(_NullSafe::isPresent)
                        .sorted(ObjectMember.Comparators.byMemberOrderSequence(false))
                        .map(ObjectAssociation::getId)
                        .collect(Collectors.toList());

                addPropertiesTo(
                        fieldSet,
                        associations1To1Ids,
                        layoutDataFactory::createPropertyLayoutData,
                        propertyLayoutDataById::put);
            }

            if(!unboundPropertyIds.isEmpty()) {
                val fieldSet = gridModel.getFieldSetForUnreferencedPropertiesRef();
                if(fieldSet != null) {
                    unboundPropertyIds.removeAll(unboundMetadataContributingIds);
                    addPropertiesTo(
                            fieldSet,
                            unboundPropertyIds,
                            layoutDataFactory::createPropertyLayoutData,
                            propertyLayoutDataById::put);
                }
            }
        }

        // any missing collections will be added as tabs to a new TabGroup in the specified column
        val surplusAndMissingCollectionIds =
                surplusAndMissing(collectionLayoutDataById.keySet(), oneToManyAssociationById.keySet());
        val surplusCollectionIds = surplusAndMissingCollectionIds.getSurplus();
        val missingCollectionIds = surplusAndMissingCollectionIds.getMissing();

        for (String surplusCollectionId : surplusCollectionIds) {
            collectionLayoutDataById.get(surplusCollectionId).setMetadataError("No such collection");
        }

        if(!missingCollectionIds.isEmpty()) {
            final List<OneToManyAssociation> sortedCollections =
                    _Lists.map(missingCollectionIds, oneToManyAssociationById::get);

            sortedCollections.sort(ObjectMember.Comparators.byMemberOrderSequence(false));

            final List<String> sortedMissingCollectionIds =
                    _Lists.map(sortedCollections, ObjectAssociation::getId);

            final BSTabGroup bsTabGroup = gridModel.getTabGroupForUnreferencedCollectionsRef();
            if(bsTabGroup != null) {
                addCollectionsTo(
                        bsTabGroup,
                        sortedMissingCollectionIds,
                        objectSpec,
                        layoutDataFactory::createCollectionLayoutData);
            } else {
                final BSCol bsCol = gridModel.getColForUnreferencedCollectionsRef();
                if(bsCol != null) {
                    addCollectionsTo(
                        bsCol,
                        sortedMissingCollectionIds,
                        layoutDataFactory::createCollectionLayoutData,
                        collectionLayoutDataById::put);
                }
            }
        }

        // any missing actions will be added as actions in the specified column
        val surplusAndMissingActionIds =
                surplusAndMissing(actionLayoutDataById.keySet(), objectActionById.keySet());
        val surplusActionIds = surplusAndMissingActionIds.getSurplus();
        val possiblyMissingActionIds = surplusAndMissingActionIds.getMissing();

        final List<String> associatedActionIds = _Lists.newArrayList();

        final List<ObjectAction> sortedPossiblyMissingActions =
                _Lists.map(possiblyMissingActionIds, objectActionById::get);

        sortedPossiblyMissingActions.sort(ObjectMember.Comparators.byMemberOrderSequence(false));

        final List<String> sortedPossiblyMissingActionIds =
                _Lists.map(sortedPossiblyMissingActions, ObjectMember::getId);

        for (final String actionId : sortedPossiblyMissingActionIds) {
            val objectAction = objectActionById.get(actionId);

            val layoutGroupFacet = objectAction.getFacet(LayoutGroupFacet.class);
            if(layoutGroupFacet == null) {
                continue;
            }
            final String layoutGroupName = layoutGroupFacet.getGroupId();
            if (layoutGroupName == null) {
                continue;
            }

            if (oneToOneAssociationById.containsKey(layoutGroupName)) {
                associatedActionIds.add(actionId);

                if(layoutGroupFacet.isExplicitBinding()) {
                    final PropertyLayoutData propertyLayoutData = propertyLayoutDataById.get(layoutGroupName);
                    if(propertyLayoutData == null) {
                        log.warn(String.format("Could not find propertyLayoutData for layoutGroupName of '%s'", layoutGroupName));
                        continue;
                    }
                    val actionLayoutData = new ActionLayoutData(actionId);
                    val actionPositionFacet = objectAction.getFacet(ActionPositionFacet.class);
                    final ActionLayoutDataOwner owner;
                    final ActionLayout.Position position;
                    if(actionPositionFacet != null) {
                        position = actionPositionFacet.position();
                        owner = position == ActionLayout.Position.PANEL
                                || position == ActionLayout.Position.PANEL_DROPDOWN
                                ? propertyLayoutData.getOwner()
                                : propertyLayoutData;
                    } else {
                        position = ActionLayout.Position.BELOW;
                        owner = propertyLayoutData;
                    }
                    actionLayoutData.setPosition(position);
                    addActionTo(owner, actionLayoutData);
                }

                continue;
            }
            if (oneToManyAssociationById.containsKey(layoutGroupName)) {
                associatedActionIds.add(actionId);

                if(layoutGroupFacet.isExplicitBinding()) {
                    val collectionLayoutData = collectionLayoutDataById.get(layoutGroupName);
                    if(collectionLayoutData==null) {
                        log.warn("failed to lookup CollectionLayoutData by layoutGroupName '{}'", layoutGroupName);
                    } else {
                        val actionLayoutData = new ActionLayoutData(actionId);
                        addActionTo(collectionLayoutData, actionLayoutData);
                    }
                }
                continue;
            }
            // if the @ActionLayout for the action references a field set (that has bound
            // associations), then don't mark it as missing, but instead explicitly add it to the
            // list of actions of that field-set.
            final Set<String> boundAssociationIds = boundAssociationIdsByFieldSetId.get(layoutGroupName);
            if(boundAssociationIds != null && !boundAssociationIds.isEmpty()) {

                associatedActionIds.add(actionId);

                final ActionLayoutData actionLayoutData = new ActionLayoutData(actionId);

                // since the action is to be associated with a fieldSet, the only available positions are PANEL and PANEL_DROPDOWN.
                // if the action already has a preference for PANEL, then preserve it, otherwise default to PANEL_DROPDOWN
                val actionPositionFacet = objectAction.getFacet(ActionPositionFacet.class);
                if(actionPositionFacet != null && actionPositionFacet.position() == ActionLayout.Position.PANEL) {
                    actionLayoutData.setPosition(ActionLayout.Position.PANEL);
                } else {
                    actionLayoutData.setPosition(ActionLayout.Position.PANEL_DROPDOWN);
                }

                final FieldSet fieldSet = gridModel.getFieldSet(layoutGroupName);
                addActionTo(fieldSet, actionLayoutData);
            }
        }

        // ... the missing actions are those in the second tuple, excluding those associated
        // (via @Action#associateWith) to a property or collection. (XXX comment might be outdated)
        final List<String> missingActionIds = _Lists.newArrayList(sortedPossiblyMissingActionIds);
        missingActionIds.removeAll(associatedActionIds);

        for (String surplusActionId : surplusActionIds) {
            actionLayoutDataById.get(surplusActionId).setMetadataError("No such action");
        }

        if(!missingActionIds.isEmpty()) {
            final BSCol bsCol = gridModel.getColForUnreferencedActionsRef();
            if(bsCol != null) {
                addActionsTo(
                        bsCol,
                        missingActionIds,
                        layoutDataFactory::createActionLayoutData,
                        actionLayoutDataById::put);
            } else {
                final FieldSet fieldSet = gridModel.getFieldSetForUnreferencedActionsRef();
                if(fieldSet != null) {
                    addActionsTo(
                            fieldSet,
                            missingActionIds,
                            layoutDataFactory::createActionLayoutData,
                            actionLayoutDataById::put);
                }
            }
        }

        return true;
    }

    private void addPropertiesTo(
            final FieldSet fieldSet,
            final Collection<String> propertyIds,
            final Function<String, PropertyLayoutData> layoutFactory,
            final BiConsumer<String, PropertyLayoutData> onNewLayoutData) {

        final Set<String> existingIds =
                stream(fieldSet.getProperties())
                .map(PropertyLayoutData::getId)
                .collect(Collectors.toSet());

        for (final String propertyId : propertyIds) {
            if(existingIds.contains(propertyId)) {
                continue;
            }
            val propertyLayoutData = layoutFactory.apply(propertyId);
            fieldSet.getProperties().add(propertyLayoutData);
            propertyLayoutData.setOwner(fieldSet);
            onNewLayoutData.accept(propertyId, propertyLayoutData);
        }
    }

    private void addCollectionsTo(
            final BSCol tabRowCol,
            final Collection<String> collectionIds,
            final Function<String, CollectionLayoutData> layoutFactory,
            final BiConsumer<String, CollectionLayoutData> onNewLayoutData) {

        for (final String collectionId : collectionIds) {
            val collectionLayoutData = layoutFactory.apply(collectionId);
            tabRowCol.getCollections().add(collectionLayoutData);
            onNewLayoutData.accept(collectionId, collectionLayoutData);
        }
    }

    private void addCollectionsTo(
            final BSTabGroup tabGroup,
            final Collection<String> collectionIds,
            final ObjectSpecification objectSpec,
            final Function<String, CollectionLayoutData> layoutFactory) {

        for (final String collectionId : collectionIds) {
            final BSTab bsTab = new BSTab();

            val feature = objectSpec.getCollectionElseFail(collectionId);
            val featureCanonicalFriendlyName = feature.getCanonicalFriendlyName();

            bsTab.setName(featureCanonicalFriendlyName);
            tabGroup.getTabs().add(bsTab);
            bsTab.setOwner(tabGroup);

            final BSRow tabRow = new BSRow();
            tabRow.setOwner(bsTab);
            bsTab.getRows().add(tabRow);

            final BSCol tabRowCol = new BSCol();
            tabRowCol.setSpan(12);
            tabRowCol.setSize(Size.MD);
            tabRowCol.setOwner(tabRow);
            tabRow.getCols().add(tabRowCol);

            val collectionLayoutData = layoutFactory.apply(collectionId);
            tabRowCol.getCollections().add(collectionLayoutData);
        }
    }

    protected void addActionsTo(
            final BSCol bsCol,
            final Collection<String> actionIds,
            final Function<String, ActionLayoutData> layoutFactory,
            final BiConsumer<String, ActionLayoutData> onNewLayoutData) {

        for (String actionId : actionIds) {
            val actionLayoutData = layoutFactory.apply(actionId);
            addActionTo(bsCol, actionLayoutData);
            onNewLayoutData.accept(actionId, actionLayoutData);
        }
    }

    private void addActionsTo(
            final FieldSet fieldSet,
            final Collection<String> actionIds,
            final Function<String, ActionLayoutData> layoutFactory,
            final BiConsumer<String, ActionLayoutData> onNewLayoutData) {

        for (String actionId : actionIds) {
            val actionLayoutData = layoutFactory.apply(actionId);
            addActionTo(fieldSet, actionLayoutData);
            onNewLayoutData.accept(actionId, actionLayoutData);
        }
    }

    private void addActionTo(
            final ActionLayoutDataOwner owner,
            final ActionLayoutData actionLayoutData) {

        List<ActionLayoutData> actions = owner.getActions();
        if(actions == null) {
            owner.setActions(actions = _Lists.newArrayList());
        }
        actions.add(actionLayoutData);
        actionLayoutData.setOwner(owner);
    }

}
