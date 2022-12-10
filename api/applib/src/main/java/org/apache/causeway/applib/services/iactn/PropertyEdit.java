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
package org.apache.causeway.applib.services.iactn;

import org.apache.causeway.applib.Identifier;
import org.apache.causeway.applib.events.domain.PropertyDomainEvent;
import org.apache.causeway.schema.common.v2.InteractionType;
import org.apache.causeway.schema.ixn.v2.PropertyEditDto;

import lombok.Getter;

/**
 * @since 1.x {@index}
 */
public class PropertyEdit
extends Execution<PropertyEditDto, PropertyDomainEvent<?, ?>> {

    @Getter
    private final Object newValue;

    public PropertyEdit(
            final Interaction interaction,
            final Identifier memberId,
            final Object target,
            final Object newValue) {
        super(interaction, InteractionType.PROPERTY_EDIT, memberId, target);
        this.newValue = newValue;
    }

    // ...
}
