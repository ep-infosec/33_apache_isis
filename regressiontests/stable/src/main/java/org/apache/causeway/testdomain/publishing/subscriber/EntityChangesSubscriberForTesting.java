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
package org.apache.causeway.testdomain.publishing.subscriber;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.stereotype.Service;

import org.apache.causeway.applib.services.publishing.spi.EntityChanges;
import org.apache.causeway.applib.services.publishing.spi.EntityChangesSubscriber;
import org.apache.causeway.applib.util.schema.ChangesDtoUtils;
import org.apache.causeway.commons.collections.Can;
import org.apache.causeway.testdomain.util.kv.KVStoreForTesting;

import lombok.val;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class EntityChangesSubscriberForTesting
implements EntityChangesSubscriber {

    @Inject private KVStoreForTesting kvStore;

    @PostConstruct
    public void init() {
        log.info("about to initialize");
    }

    @Override
    public void onChanging(EntityChanges publishedObjects) {

        @SuppressWarnings("unchecked")
        val publishedEntries =
        (List<EntityChanges>) kvStore.get(this, "publishedObjects").orElseGet(ArrayList::new);

        publishedEntries.add(publishedObjects);

        kvStore.put(this, "publishedObjects", publishedEntries);
        log.debug("publish objects {}", ()->ChangesDtoUtils.toXml(publishedObjects.getDto()));

    }

    // -- UTILITIES

    @SuppressWarnings("unchecked")
    public static Can<EntityChanges> getPublishedObjects(KVStoreForTesting kvStore) {
        return Can.ofCollection(
                (List<EntityChanges>) kvStore.get(EntityChangesSubscriberForTesting.class, "publishedObjects")
                .orElse(null));
    }

    public static void clearPublishedEntries(KVStoreForTesting kvStore) {
        kvStore.clear(EntityChangesSubscriberForTesting.class);
    }

    public static int getCreated(KVStoreForTesting kvStore) {
        val publishedObjects = getPublishedObjects(kvStore);
        return publishedObjects.stream().mapToInt(EntityChanges::getNumberCreated).sum();
    }

    public static int getDeleted(KVStoreForTesting kvStore) {
        val publishedObjects = getPublishedObjects(kvStore);
        return publishedObjects.stream().mapToInt(EntityChanges::getNumberDeleted).sum();
    }

    public static int getLoaded(KVStoreForTesting kvStore) {
        val publishedObjects = getPublishedObjects(kvStore);
        return publishedObjects.stream().mapToInt(EntityChanges::getNumberLoaded).sum();
    }

    public static int getUpdated(KVStoreForTesting kvStore) {
        val publishedObjects = getPublishedObjects(kvStore);
        return publishedObjects.stream().mapToInt(EntityChanges::getNumberUpdated).sum();
    }

    public static int getModified(KVStoreForTesting kvStore) {
        val publishedObjects = getPublishedObjects(kvStore);
        return publishedObjects.stream().mapToInt(EntityChanges::getNumberPropertiesModified).sum();
    }


}