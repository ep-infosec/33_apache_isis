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
package org.apache.causeway.core.runtime.events;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import org.apache.causeway.applib.services.eventbus.EventBusService;
import org.apache.causeway.applib.services.iactn.Interaction;
import org.apache.causeway.applib.services.iactnlayer.InteractionLayerTracker;
import org.apache.causeway.core.interaction.scope.TransactionBoundaryAware;
import org.apache.causeway.core.transaction.events.TransactionAfterCompletionEvent;
import org.apache.causeway.core.transaction.events.TransactionBeforeCompletionEvent;

import lombok.RequiredArgsConstructor;
import lombok.val;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class TransactionEventEmitter
implements TransactionSynchronization, TransactionBoundaryAware {

    private final EventBusService eventBusService;
    private final InteractionLayerTracker interactionLayerTracker;

    @Override
    public void beforeCompletion() {
        _Xray.txBeforeCompletion(interactionLayerTracker, "tx: beforeCompletion");
        eventBusService.post(TransactionBeforeCompletionEvent.instance());
    }

    @Override
    public void afterCompletion(final int status) {
        val event = TransactionAfterCompletionEvent.forStatus(status);
        eventBusService.post(event);
        _Xray.txAfterCompletion(interactionLayerTracker, String.format("tx: afterCompletion (%s)", event.name()));
    }

    @Override
    public void afterEnteringTransactionalBoundary(
            final Interaction interaction,
            final boolean isSynchronizationActive) {
        if(isSynchronizationActive) {
            TransactionSynchronizationManager.registerSynchronization(this);
        }
    }

}
