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
package demoapp.dom.domain.collections.Collection.domainEvent;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.causeway.applib.annotation.Collection;
import org.apache.causeway.applib.annotation.CollectionLayout;
import org.apache.causeway.applib.annotation.DomainObject;
import org.apache.causeway.applib.annotation.Editing;
import org.apache.causeway.applib.annotation.Nature;
import org.apache.causeway.applib.annotation.ObjectSupport;
import org.apache.causeway.applib.events.domain.CollectionDomainEvent;

import demoapp.dom._infra.asciidocdesc.HasAsciiDocDescription;
import demoapp.dom.domain.collections.Collection.domainEvent.child.CollectionDomainEventChildVm;
import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name = "root")
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
@Named("demo.CollectionDomainEventVm")
@DomainObject(
    nature=Nature.VIEW_MODEL,
    editing = Editing.ENABLED
)
//tag::class[]
public class CollectionDomainEventVm implements HasAsciiDocDescription {
    // ...
//end::class[]

    @ObjectSupport public String title() {
        return "Collection#domainEvent";
    }
//tag::class[]

    public static class ChildrenDomainEvent             // <.>
        extends CollectionDomainEvent<CollectionDomainEventVm,CollectionDomainEventChildVm> {}

//tag::children[]
    @Collection(
        domainEvent = ChildrenDomainEvent.class         // <.>
    )
    @CollectionLayout(
        describedAs = "@Collection(domainEvent = ChildrenDomainEvent.class)"
    )
    @XmlElementWrapper(name = "children")
    @XmlElement(name = "child")
    @Getter @Setter
    private List<CollectionDomainEventChildVm> children = new ArrayList<>();
//end::children[]

    int lastChildNumberAdded;

}
//end::class[]
