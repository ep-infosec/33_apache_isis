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
package org.apache.causeway.core.metamodel.services.tablecol;

import java.util.List;

import javax.annotation.Priority;
import javax.inject.Named;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import org.apache.causeway.applib.annotation.PriorityPrecedence;
import org.apache.causeway.applib.services.tablecol.TableColumnOrderService;
import org.apache.causeway.core.metamodel.CausewayModuleCoreMetamodel;

/**
 * Used as a identity fallback.
 *
 * @since 1.x {@index}
 */
@Service
@Named(CausewayModuleCoreMetamodel.NAMESPACE + ".TableColumnOrderServiceDefault")
@Priority(PriorityPrecedence.LATE)
@Qualifier("Default")
public class TableColumnOrderServiceDefault implements TableColumnOrderService {

    /**
     * Just returns the <code>propertyIds</code> unchanged.
     *
     * @param parent
     * @param collectionId
     * @param collectionType
     * @param propertyIds
     */
    @Override
    public List<String> orderParented(
            final Object parent,
            final String collectionId,
            final Class<?> collectionType,
            final List<String> propertyIds) {
        return propertyIds;
    }

    /**
     * Just returns the <code>propertyIds</code> unchanged.
     *
     * @param collectionType
     * @param propertyIds
     */
    @Override
    public List<String> orderStandalone(
            final Class<?> collectionType,
            final List<String> propertyIds) {
        return propertyIds;
    }
}
