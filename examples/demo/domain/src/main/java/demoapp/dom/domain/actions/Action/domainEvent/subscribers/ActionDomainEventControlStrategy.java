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
package demoapp.dom.domain.actions.Action.domainEvent.subscribers;

import java.util.List;

import org.apache.causeway.applib.events.domain.ActionDomainEvent;
import org.apache.causeway.applib.services.message.MessageService;
import org.apache.causeway.applib.services.registry.ServiceRegistry;

import demoapp.dom.domain.actions.Action.domainEvent.ActionDomainEventVm;
import demoapp.dom.domain.actions.Action.domainEvent.ActionDomainEventVm_mixinUpdateText;

// tag::class[]
enum ActionDomainEventControlStrategy {

    DO_NOTHING{
        @Override
        void on(ActionDomainEvent<?> ev
                , ServiceRegistry serviceRegistry) {
        }
    },
    // ...
// end::class[]

// tag::hide[]
    HIDE_BOTH {
        @Override
        void on(ActionDomainEvent<?> ev
                , ServiceRegistry serviceRegistry) {
            switch (ev.getEventPhase()) {
                case HIDE:
                    ev.hide();
                    break;
            }
        }
    },
    HIDE_REGULAR_ACTION {
        @Override
        void on(ActionDomainEvent<?> ev
                , ServiceRegistry serviceRegistry) {
            if (ev instanceof ActionDomainEventVm.UpdateTextDomainEvent) {
                switch (ev.getEventPhase()) {
                    case HIDE:
                        ev.hide();
                        break;
                }
            }
        }
    },
// end::hide[]
// tag::disable[]
    DISABLE_BOTH {
        @Override
        void on(ActionDomainEvent<?> ev
                , ServiceRegistry serviceRegistry) {
            switch (ev.getEventPhase()) {
                case DISABLE:
                    ev.disable("ControlStrategy set to DISABLE_BOTH");
                    break;
            }

        }
    },
    DISABLE_MIXIN_ACTION {
        @Override
        void on(ActionDomainEvent<?> ev
                , ServiceRegistry serviceRegistry) {
            if(ev instanceof ActionDomainEventVm_mixinUpdateText.DomainEvent) {
                switch (ev.getEventPhase()) {
                    case DISABLE:
                        ev.disable("ControlStrategy set to DISABLE_MIXIN_ACTION");
                        break;
                }
            }

        }
    },
// end::disable[]
// tag::validate[]
    VALIDATE_MUST_BE_UPPER_CASE{
        @Override
        void on(ActionDomainEvent<?> ev
                , ServiceRegistry serviceRegistry) {
            switch (ev.getEventPhase()) {
                case VALIDATE:
                    String argument = (String) ev.getArguments().get(0);
                    if(!argument.toUpperCase().equals(argument)) {
                        ev.invalidate("must be upper case");
                    }
                    break;
            }

        }
    },
// end::validate[]
// tag::executing[]
    EXECUTING_FORCE_UPPER_CASE{
        @Override
        void on(ActionDomainEvent<?> ev
                , ServiceRegistry serviceRegistry) {

            switch (ev.getEventPhase()) {
                case EXECUTING:
                    List<Object> arguments = ev.getArguments();
                    String newValue = ((String) arguments.get(0)).toUpperCase();
                    arguments.set(0, newValue);
                    break;
            }
        }
    },
// end::executing[]
// tag::executed[]
    EXECUTED_ANNOUNCE{
        @Override
        void on(ActionDomainEvent<?> ev
                , ServiceRegistry serviceRegistry) {
            switch (ev.getEventPhase()) {
                case EXECUTED:
                    serviceRegistry
                        .lookupService(MessageService.class)
                        .ifPresent(ms ->
                                ms.informUser("Changed using updateText")
                        );
                    break;
            }
        }
    }
// end::executed[]

// tag::class[]
    ;
    abstract void on(ActionDomainEvent<?> ev
            , ServiceRegistry serviceRegistry);
}
// end::class[]
