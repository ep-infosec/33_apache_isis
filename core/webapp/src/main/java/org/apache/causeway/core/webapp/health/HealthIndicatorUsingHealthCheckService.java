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
package org.apache.causeway.core.webapp.health;

import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

import org.apache.causeway.applib.services.health.HealthCheckService;
import org.apache.causeway.applib.services.iactnlayer.InteractionService;
import org.apache.causeway.core.security.authentication.InteractionContextFactory;

import lombok.val;

@Component
@Named("causeway.webapp.HealthCheckService") // logical name appears in the endpoint
public class HealthIndicatorUsingHealthCheckService extends AbstractHealthIndicator {

    private final InteractionService interactionService;
    private final Optional<HealthCheckService> healthCheckServiceIfAny;

    @Inject
    public HealthIndicatorUsingHealthCheckService(
            final InteractionService interactionService,
            final Optional<HealthCheckService> healthCheckServiceIfAny) {
        this.interactionService = interactionService;
        this.healthCheckServiceIfAny = healthCheckServiceIfAny;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        val health = healthCheckServiceIfAny
                .map(healthCheckService ->
                    interactionService
                        .call(InteractionContextFactory.health(), healthCheckService::check))
                .orElse(null);
        if(health != null) {
            final boolean result = health.getResult();
            if(result) {
                builder.up();
            } else {
                final Throwable cause = health.getCause();
                if(cause != null) {
                    builder.down(cause);
                } else {
                    builder.down();
                }
            }
        } else {
            builder.unknown();
        }
    }
}
