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
package org.apache.causeway.testdomain.transactions.jdo;

import javax.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.causeway.applib.services.iactnlayer.InteractionService;
import org.apache.causeway.applib.services.repository.RepositoryService;
import org.apache.causeway.applib.services.xactn.TransactionService;
import org.apache.causeway.core.config.presets.CausewayPresets;
import org.apache.causeway.testdomain.conf.Configuration_usingJdo;
import org.apache.causeway.testdomain.jdo.JdoTestFixtures;
import org.apache.causeway.testdomain.jdo.JdoTestFixtures.Lock;
import org.apache.causeway.testdomain.jdo.entities.JdoBook;
import org.apache.causeway.testdomain.util.interaction.InteractionBoundaryProbe;
import org.apache.causeway.testdomain.util.kv.KVStoreForTesting;

@SpringBootTest(
        classes = {
                Configuration_usingJdo.class,
                InteractionBoundaryProbe.class
        })
//@Transactional
@TestPropertySource(CausewayPresets.UseLog4j2Test)
/**
 * With this test we manage CausewayInteractions ourselves. (not sub-classing CausewayIntegrationTestAbstract)
 */
class JdoTransactionScopeListenerTest {

    @Inject private JdoTestFixtures jdoTestFixtures;
    @Inject private TransactionService transactionService;
    @Inject private RepositoryService repository;
    @Inject private InteractionService interactionService;
    @Inject private KVStoreForTesting kvStoreForTesting;
    private Lock lock;

    /* Expectations:
     * 1. for each InteractionScope there should be a new InteractionBoundaryProbe instance
     * 2. for each Transaction the current InteractionBoundaryProbe should get notified
     *
     * first we have 1 InteractionScope with 1 expected Transaction during 'setUp'
     * then we have 1 InteractionScope with 3 expected Transactions within the test method
     *
     */

    @BeforeEach
    void setUp() {
        // new InteractionScope with a new transaction (#1)
        // clear repository
        lock = jdoTestFixtures.clearAndAquireLock();
    }

    @AfterEach
    void restore() {
        // restore repository
        lock.release();
    }

    @Test
    void sessionScopedProbe_shouldBeReused_andBeAwareofTransactionBoundaries() {

        // new InteractionScope with a new transaction (#2)
        interactionService.runAnonymous(()->{

            // expected pre condition
            // reuse transaction (#2)
            assertEquals(0, repository.allInstances(JdoBook.class).size());

            // reuse transaction (#2)
            transactionService.runWithinCurrentTransactionElseCreateNew(()->{
                // + 1 interaction + 1 transaction
                jdoTestFixtures.install(lock);
            });

            // expected post condition
            // reuse transaction (#2)
            assertEquals(3, repository.allInstances(JdoBook.class).size());

        });

        assertEquals(3, InteractionBoundaryProbe.totalInteractionsStarted(kvStoreForTesting));
        assertEquals(3, InteractionBoundaryProbe.totalInteractionsEnded(kvStoreForTesting));
        assertEquals(3, InteractionBoundaryProbe.totalTransactionsEnding(kvStoreForTesting));
        assertEquals(3, InteractionBoundaryProbe.totalTransactionsCommitted(kvStoreForTesting));

    }


}
