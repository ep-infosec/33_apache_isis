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
package org.apache.causeway.core.runtimeservices.wrapper.proxy;

import java.lang.reflect.Proxy;

import org.apache.causeway.applib.services.wrapper.WrappingObject;
import org.apache.causeway.commons.internal.base._Casts;
import org.apache.causeway.commons.internal.collections._Arrays;
import org.apache.causeway.commons.internal.proxy._ProxyFactory;
import org.apache.causeway.commons.internal.proxy._ProxyFactoryService;
import org.apache.causeway.core.runtimeservices.wrapper.handlers.DelegatingInvocationHandler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProxyCreator {

    @NonNull private final _ProxyFactoryService proxyFactoryService;

    public <T> T instantiateProxy(final DelegatingInvocationHandler<T> handler) {

        final T toProxy = handler.getDelegate();

        final Class<T> base = _Casts.uncheckedCast(toProxy.getClass());

        if (base.isInterface()) {
            return _Casts.uncheckedCast(
                    Proxy.newProxyInstance(
                            base.getClassLoader(),
                            _Arrays.combine(base, (Class<?>[]) new Class[]{WrappingObject.class}),
                            handler));
        } else {
            final _ProxyFactory<T> proxyFactory = proxyFactoryService.factory(base, WrappingObject.class);
            return proxyFactory.createInstance(handler, false);
        }
    }

}
