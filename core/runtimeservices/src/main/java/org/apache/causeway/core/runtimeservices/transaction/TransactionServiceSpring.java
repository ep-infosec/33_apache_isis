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
package org.apache.causeway.core.runtimeservices.transaction;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.LongAdder;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import org.apache.causeway.applib.annotation.PriorityPrecedence;
import org.apache.causeway.applib.services.iactn.Interaction;
import org.apache.causeway.applib.services.iactnlayer.InteractionLayerTracker;
import org.apache.causeway.applib.services.xactn.TransactionId;
import org.apache.causeway.applib.services.xactn.TransactionService;
import org.apache.causeway.applib.services.xactn.TransactionState;
import org.apache.causeway.commons.collections.Can;
import org.apache.causeway.commons.functional.Try;
import org.apache.causeway.commons.internal.base._NullSafe;
import org.apache.causeway.commons.internal.exceptions._Exceptions;
import org.apache.causeway.core.interaction.scope.TransactionBoundaryAware;
import org.apache.causeway.core.runtimeservices.CausewayModuleCoreRuntimeServices;
import org.apache.causeway.core.transaction.events.TransactionAfterCompletionEvent;

import lombok.val;
import lombok.extern.log4j.Log4j2;

/**
 * @implNote This implementation yet does not support more than one {@link PlatformTransactionManager}
 * on the same Spring context. If more than one are discovered, some methods will fail
 * with {@link IllegalStateException}s.
 *
 * @since 2.0 {@index}
 *
 */
@Service
@Named(CausewayModuleCoreRuntimeServices.NAMESPACE + ".TransactionServiceSpring")
@Priority(PriorityPrecedence.MIDPOINT)
@Qualifier("Spring")
@Log4j2
public class TransactionServiceSpring
implements
    TransactionService,
    TransactionBoundaryAware {

    private final Can<PlatformTransactionManager> platformTransactionManagers;
    private final InteractionLayerTracker interactionLayerTracker;
    private final Can<PersistenceExceptionTranslator> persistenceExceptionTranslators;


    @Inject
    public TransactionServiceSpring(
            final List<PlatformTransactionManager> platformTransactionManagers,
            final List<PersistenceExceptionTranslator> persistenceExceptionTranslators,
            final InteractionLayerTracker interactionLayerTracker) {

        this.platformTransactionManagers = Can.ofCollection(platformTransactionManagers);
        log.info("PlatformTransactionManagers: {}", platformTransactionManagers);

        this.persistenceExceptionTranslators = Can.ofCollection(persistenceExceptionTranslators);
        log.info("PersistenceExceptionTranslators: {}", persistenceExceptionTranslators);

        this.interactionLayerTracker = interactionLayerTracker;
    }

    // -- SPRING INTEGRATION

    @Override
    public <T> Try<T> callTransactional(final TransactionDefinition def, final Callable<T> callable) {

        val txManager = transactionManagerForElseFail(def); // always throws if configuration is wrong

        Try<T> result = null;

        try {

            val tx = txManager.getTransaction(def);

            result = Try.call(callable)
                    .mapFailure(ex->translateExceptionIfPossible(ex, txManager));

            if(result.isFailure()) {
                txManager.rollback(tx);
            } else {
                txManager.commit(tx);
            }

        } catch (Exception ex) {

            return result!=null
                    && result.isFailure()

                    // return the original failure cause (originating from calling the callable)
                    // (so we don't shadow the original failure)
                    ? result

                    // return the failure we just catched
                    : Try.failure(translateExceptionIfPossible(ex, txManager));

        }

        return result;
    }

//    @Override
//    public void nextTransaction() {
//
//        val txManager = singletonTransactionManagerElseFail();
//
//        try {
//
//            val txTemplate = new TransactionTemplate(txManager);
//            txTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
//
//            // either reuse existing or create new
//            val txStatus = txManager.getTransaction(txTemplate);
//            if(txStatus.isNewTransaction()) {
//                // we have created a new transaction, so we are done
//                return;
//            }
//            // we are reusing an exiting transaction, so end it and create a new one afterwards
//            if(txStatus.isRollbackOnly()) {
//                txManager.rollback(txStatus);
//            } else {
//                //XXX we removed the entire method, because of following subtlety
                  // If the transaction wasn't a new one, omit the commit for proper participation in
//                // the surrounding transaction. If a previous transaction has been suspended to be
//                // able to create a new one, resume the previous transaction after committing the new one.
//                txManager.commit(txStatus);
//           }
//
//            // begin a new transaction
//            txManager.getTransaction(txTemplate);
//
//        } catch (RuntimeException ex) {
//
//            val translatedEx = translateExceptionIfPossible(ex, txManager);
//
//            if(translatedEx instanceof RuntimeException) {
//                throw ex;
//            }
//
//            throw new RuntimeException(ex);
//
//        }
//
//    }

    @Override
    public void flushTransaction() {

        try {

            log.debug("about to flush tx");

            currentTransactionStatus()
                .ifPresent(TransactionStatus::flush);

        } catch (RuntimeException ex) {

            val txManager = singletonTransactionManagerElseFail();

            val translatedEx = translateExceptionIfPossible(ex, txManager);

            if(translatedEx instanceof RuntimeException) {
                throw ex;
            }

            throw new RuntimeException(ex);

        }
    }


    @Override
    public Optional<TransactionId> currentTransactionId() {
        return interactionLayerTracker.getInteractionId()
                .map(uuid->{
                    //XXX get current transaction's persistence context (once we support multiple contexts)
                    val persistenceContext = "";
                    return TransactionId.of(uuid, txCounter.get().intValue(), persistenceContext);
                });
    }

    @Override
    public TransactionState currentTransactionState() {

        return currentTransactionStatus()
        .map(txStatus->{

            if(txStatus.isCompleted()) {
                return txStatus.isRollbackOnly()
                        ? TransactionState.ABORTED
                        : TransactionState.COMMITTED;
            }

            return txStatus.isRollbackOnly()
                    ? TransactionState.MUST_ABORT
                    : TransactionState.IN_PROGRESS;

        })
        .orElse(TransactionState.NONE);
    }

    // -- TRANSACTION SEQUENCE TRACKING

    private ThreadLocal<LongAdder> txCounter = ThreadLocal.withInitial(LongAdder::new);

    /** INTERACTION BEGIN BOUNDARY */
    @Override
    public void beforeEnteringTransactionalBoundary(final Interaction interaction) {
        txCounter.get().reset();
    }

    /** TRANSACTION END BOUNDARY */
    @EventListener(TransactionAfterCompletionEvent.class)
    public void onTransactionEnded(final TransactionAfterCompletionEvent event) {
        txCounter.get().increment();
    }

    /** INTERACTION END BOUNDARY */
    @Override
    public void afterLeavingTransactionalBoundary(final Interaction interaction) {
        txCounter.remove(); //XXX not tested yet: can we be certain that no txCounter.get() is called afterwards?
    }

    // -- HELPER

    private PlatformTransactionManager transactionManagerForElseFail(final TransactionDefinition def) {
        if(def instanceof TransactionTemplate) {
            val txManager = ((TransactionTemplate)def).getTransactionManager();
            if(txManager!=null) {
                return txManager;
            }
        }
        return platformTransactionManagers.getSingleton()
                .orElseThrow(()->
                    platformTransactionManagers.getCardinality().isMultiple()
                        ? _Exceptions.illegalState("Multiple PlatformTransactionManagers are configured, "
                                + "make sure a PlatformTransactionManager is provided via the TransactionTemplate argument.")
                        : _Exceptions.illegalState("Needs a PlatformTransactionManager."));
    }

    private PlatformTransactionManager singletonTransactionManagerElseFail() {
        return platformTransactionManagers.getSingleton()
                .orElseThrow(()->
                    platformTransactionManagers.getCardinality().isMultiple()
                        ? _Exceptions.illegalState("Multiple PlatformTransactionManagers are configured, "
                                + "cannot reason about which one to use.")
                        : _Exceptions.illegalState("Needs a PlatformTransactionManager."));
    }

    private Optional<TransactionStatus> currentTransactionStatus() {

        val txManager = singletonTransactionManagerElseFail();
        val txTemplate = new TransactionTemplate(txManager);
        txTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_MANDATORY);

        // not strictly required, but to prevent stack-trace creation later on
        if(!TransactionSynchronizationManager.isActualTransactionActive()) {
            return Optional.empty();
        }

        // get current transaction else throw an exception
        return Try.call(()->
                //XXX creating stack-traces is expensive
                txManager.getTransaction(txTemplate))
                .getValue();

    }

    private Throwable translateExceptionIfPossible(final Throwable ex, final PlatformTransactionManager txManager) {

        if(ex instanceof DataAccessException) {
            return ex; // nothing to do, already translated
        }

        if(ex instanceof RuntimeException) {

            val translatedEx = persistenceExceptionTranslators.stream()
            //.peek(translator->System.out.printf("%s", translator.getClass().getName()))
            .map(translator->translator.translateExceptionIfPossible((RuntimeException)ex))
            .filter(_NullSafe::isPresent)
            .findFirst()
            .orElse(null);

            if(translatedEx!=null) {
                return translatedEx;
            }

        }

        return ex;
    }

}
