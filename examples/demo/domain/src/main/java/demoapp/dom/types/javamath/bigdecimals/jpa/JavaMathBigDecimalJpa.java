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
package demoapp.dom.types.javamath.bigdecimals.jpa;

import javax.inject.Named;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.context.annotation.Profile;

import org.apache.causeway.applib.annotation.DomainObject;
import org.apache.causeway.applib.annotation.Editing;
import org.apache.causeway.applib.annotation.Optionality;
import org.apache.causeway.applib.annotation.Property;
import org.apache.causeway.applib.annotation.PropertyLayout;
import org.apache.causeway.applib.annotation.Title;
import org.apache.causeway.applib.annotation.ValueSemantics;
import org.apache.causeway.persistence.jpa.applib.integration.CausewayEntityListener;

import demoapp.dom.types.javamath.bigdecimals.persistence.JavaMathBigDecimalEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Profile("demo-jpa")
//tag::class[]
@Entity
@Table(
      schema = "demo",
      name = "JavaMathBigDecimalJpa"
)
@EntityListeners(CausewayEntityListener.class)
@Named("demo.JavaMathBigDecimalEntity")
@DomainObject
@NoArgsConstructor
public class JavaMathBigDecimalJpa                                           // <.>
        extends JavaMathBigDecimalEntity {

//end::class[]
    public JavaMathBigDecimalJpa(final java.math.BigDecimal initialValue) {
        this.readOnlyProperty = initialValue;
        this.readWriteProperty = initialValue;
        this.withMax2FractionDigits = initialValue;
        this.withFixed2FractionDigits = initialValue;
    }

//tag::class[]
    @Id
    @GeneratedValue
    private Long id;

    @Title(prepend = "java.math.BigDecimalJpa entity: ")
    @PropertyLayout(fieldSetId = "read-only-properties", sequence = "1")
    @Column(nullable = false)                                               // <.>
    @Getter @Setter
    private java.math.BigDecimal readOnlyProperty;

    @Property(editing = Editing.ENABLED)                                    // <.>
    @PropertyLayout(fieldSetId = "editable-properties", sequence = "1")
    @Column(nullable = false)
    @Getter @Setter
    private java.math.BigDecimal readWriteProperty;

    @Property(editing = Editing.ENABLED)
    @PropertyLayout(fieldSetId = "editable-properties", sequence = "2",
            describedAs = "has a maximum of 2 fraction digits (scale=2)")
    @Column(nullable = false, scale = 2)                                    // <.>
    @Getter @Setter
    private java.math.BigDecimal withMax2FractionDigits;

    @Property(editing = Editing.ENABLED)
    @ValueSemantics(minFractionalDigits = 2, maxFractionalDigits = 2)
    @PropertyLayout(fieldSetId = "editable-properties", sequence = "3",
            describedAs = "has 2 fixed fraction digits (scale=2)")
    @Column(nullable = false, scale = 2)
    @Getter @Setter
    private java.math.BigDecimal withFixed2FractionDigits;

    @Property(optionality = Optionality.OPTIONAL)                           // <.>
    @PropertyLayout(fieldSetId = "optional-properties", sequence = "1")
    @Column(nullable = true)                                                // <.>
    @Getter @Setter
    private java.math.BigDecimal readOnlyOptionalProperty;

    @Property(editing = Editing.ENABLED, optionality = Optionality.OPTIONAL)
    @PropertyLayout(fieldSetId = "optional-properties", sequence = "2")
    @Column(nullable = true)
    @Getter @Setter
    private java.math.BigDecimal readWriteOptionalProperty;

}
//end::class[]
