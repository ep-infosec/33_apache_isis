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
package demoapp.dom.domain.objects.other.mixins;

import javax.inject.Named;

import org.apache.causeway.applib.annotation.DomainObject;
import org.apache.causeway.applib.annotation.Editing;
import org.apache.causeway.applib.annotation.Nature;
import org.apache.causeway.applib.annotation.ObjectSupport;
import org.apache.causeway.applib.annotation.Property;
import org.apache.causeway.applib.annotation.Where;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Named("demo.FibonacciNumberVm")
@DomainObject(
        nature=Nature.VIEW_MODEL,
        editing = Editing.DISABLED)
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class FibonacciNumberVm {

    @ObjectSupport public String title() {
        return String.format("%d ! = %d", getNumber(), getFibonacci());
    }

    @Property()
    @Getter @Setter
    private int number;

    @Property()
    @Getter @Setter
    private int fibonacci;

    @Property(hidden = Where.REFERENCES_PARENT)
    @Getter @Setter
    private CountHolder parent;

}
