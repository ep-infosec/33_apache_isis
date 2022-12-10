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
package demoapp.dom.domain.actions.Action.executionPublishing.jdo;

import javax.inject.Named;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;

import org.springframework.context.annotation.Profile;

import org.apache.causeway.applib.annotation.Action;
import org.apache.causeway.applib.annotation.ActionLayout;
import org.apache.causeway.applib.annotation.DomainObject;
import org.apache.causeway.applib.annotation.Editing;
import org.apache.causeway.applib.annotation.MemberSupport;
import org.apache.causeway.applib.annotation.Nature;
import org.apache.causeway.applib.annotation.ObjectSupport;
import org.apache.causeway.applib.annotation.Property;
import org.apache.causeway.applib.annotation.PropertyLayout;
import org.apache.causeway.applib.annotation.Publishing;
import org.apache.causeway.applib.annotation.SemanticsOf;

import demoapp.dom.domain.actions.Action.executionPublishing.ActionExecutionPublishingDisabledMetaAnnotation;
import demoapp.dom.domain.actions.Action.executionPublishing.ActionExecutionPublishingEnabledMetaAnnotation;
import demoapp.dom.domain.actions.Action.executionPublishing.ActionExecutionPublishingEntity;
import lombok.Getter;
import lombok.Setter;

@Profile("demo-jdo")
//tag::class[]
@PersistenceCapable(identityType = IdentityType.DATASTORE, schema = "demo")
@DatastoreIdentity(strategy = IdGeneratorStrategy.IDENTITY, column = "id")
@Named("demo.ActionExecutionPublishingEntity")
@DomainObject(
        nature=Nature.ENTITY
        , editing = Editing.DISABLED
)
public class ActionExecutionPublishingJdo
        extends ActionExecutionPublishingEntity {
    // ...
//end::class[]

    public ActionExecutionPublishingJdo(final String initialValue) {
        this.property = initialValue;
        this.propertyMetaAnnotated = initialValue;
        this.propertyMetaAnnotatedOverridden = initialValue;
    }

    @ObjectSupport public String title() {
        return "Action#executionPublishing (JDO)";
    }

//tag::property[]
    @Property()
    @PropertyLayout(fieldSetId = "annotation", sequence = "1")
    @Getter @Setter
    private String property;

    @Property()
    @PropertyLayout(fieldSetId = "meta-annotated", sequence = "1")
    @Getter @Setter
    private String propertyMetaAnnotated;

    @Property()
    @PropertyLayout(fieldSetId = "meta-annotated-overridden", sequence = "1")
    @Getter @Setter
    private String propertyMetaAnnotatedOverridden;
//end::property[]

//tag::annotation[]
    @Action(
        executionPublishing = Publishing.ENABLED       // <.>
        , semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(
        named = "Update Property"
        , describedAs = "@Action(publishing = ENABLED)"
        , associateWith = "property"
        , sequence = "1"
    )
    public ActionExecutionPublishingJdo updatePropertyUsingAnnotation(final String value) {
        setProperty(value);
        return this;
    }
    @MemberSupport public String default0UpdatePropertyUsingAnnotation() {
        return getProperty();
    }

//end::annotation[]

//tag::meta-annotation[]
    @ActionExecutionPublishingEnabledMetaAnnotation      // <.>
    @Action(
        semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(
        named = "Update Property"
        , describedAs = "@ActionPublishingEnabledMetaAnnotation"
        , associateWith = "propertyMetaAnnotated"
        , sequence = "1"
    )
    public ActionExecutionPublishingJdo updatePropertyUsingMetaAnnotation(final String value) {
        setPropertyMetaAnnotated(value);
        return this;
    }
    @MemberSupport public String default0UpdatePropertyUsingMetaAnnotation() {
        return getPropertyMetaAnnotated();
    }
//end::meta-annotation[]

//tag::meta-annotation-overridden[]
    @ActionExecutionPublishingDisabledMetaAnnotation     // <.>
    @Action(
        executionPublishing = Publishing.ENABLED       // <.>
        , semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(
        named = "Update Property"
        , describedAs =
            "@ActionPublishingDisabledMetaAnnotation " +
            "@Action(publishing = ENABLED)"
        , associateWith = "propertyMetaAnnotatedOverridden"
        , sequence = "1"
    )
    public ActionExecutionPublishingJdo updatePropertyUsingMetaAnnotationButOverridden(final String value) {
        setPropertyMetaAnnotatedOverridden(value);
        return this;
    }
    @MemberSupport public String default0UpdatePropertyUsingMetaAnnotationButOverridden() {
        return getPropertyMetaAnnotatedOverridden();
    }
//end::meta-annotation-overridden[]

//tag::class[]
}
//end::class[]
