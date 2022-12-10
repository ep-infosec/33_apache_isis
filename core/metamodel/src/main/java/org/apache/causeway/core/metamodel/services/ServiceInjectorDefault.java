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
package org.apache.causeway.core.metamodel.services;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import org.apache.causeway.applib.annotation.PriorityPrecedence;
import org.apache.causeway.applib.services.inject.ServiceInjector;
import org.apache.causeway.core.metamodel.CausewayModuleCoreMetamodel;
import org.apache.causeway.core.metamodel.object.ManagedObject;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 *
 * @since 2.0
 *
 */
@Service
@Named(CausewayModuleCoreMetamodel.NAMESPACE + ".ServiceInjectorDefault")
@Priority(PriorityPrecedence.EARLY)
@Qualifier("Default")
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ServiceInjectorDefault implements ServiceInjector {

    private final AutowireCapableBeanFactory autowireCapableBeanFactory;

    @Override
    public <T> T injectServicesInto(final @Nullable T domainObject) {

        if(domainObject!=null) {
            if(domainObject instanceof ManagedObject) {
                // in case a ManagedObject was passed instead of the pojo.
                val managedObject = (ManagedObject) domainObject;
                val actualDomainObject = managedObject.getPojo();
                if(actualDomainObject != null) {
                    injectInto(actualDomainObject);
                }
            } else {
                injectInto(domainObject);
            }
        }

        return domainObject;
    }

    private <T> void injectInto(final @NonNull T domainObject) {
        autowireCapableBeanFactory.autowireBeanProperties(
                domainObject,
                AutowireCapableBeanFactory.AUTOWIRE_NO,
                /*dependencyCheck*/ false);
    }

}
