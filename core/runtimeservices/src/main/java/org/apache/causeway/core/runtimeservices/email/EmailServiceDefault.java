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
package org.apache.causeway.core.runtimeservices.email;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.activation.DataSource;
import javax.annotation.PostConstruct;
import javax.annotation.Priority;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.ImageHtmlEmail;
import org.apache.commons.mail.resolver.DataSourceClassPathResolver;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import org.apache.causeway.applib.annotation.PriorityPrecedence;
import org.apache.causeway.applib.services.email.EmailService;
import org.apache.causeway.commons.internal.base._Strings;
import org.apache.causeway.core.config.CausewayConfiguration;
import org.apache.causeway.core.runtimeservices.CausewayModuleCoreRuntimeServices;

import lombok.extern.log4j.Log4j2;

/**
 * A service that sends email notifications when specific events occur
 */
@Service
@Named(CausewayModuleCoreRuntimeServices.NAMESPACE + ".EmailServiceDefault")
@Priority(PriorityPrecedence.MIDPOINT)
@Qualifier("Default")
@Log4j2
public class EmailServiceDefault implements EmailService {

    private static final long serialVersionUID = 1L;
    public static class EmailServiceException extends RuntimeException {
        static final long serialVersionUID = 1L;
        public EmailServiceException(final EmailException cause) {
            super(cause);
        }
    }

    @Inject private CausewayConfiguration configuration;

    // -- INIT

    private boolean initialized;

    /**
     * Loads responsive email templates borrowed from http://zurb.com/ink/templates.php (Basic)
     */
    @Override
    @PostConstruct
    public void init() {

        if (initialized) {
            return;
        }

        initialized = true;

        if (!isConfigured()) {
            log.warn("NOT configured");
        } else {
            log.debug("configured");
        }
    }

    protected String getSenderEmailUsername() {
        return configuration.getCore().getRuntimeServices().getEmail().getSender().getUsername();
    }

    protected String getSenderEmailAddress() {
        return configuration.getCore().getRuntimeServices().getEmail().getSender().getAddress();
    }

    protected String getSenderEmailPassword() {
        return configuration.getCore().getRuntimeServices().getEmail().getSender().getPassword();
    }

    protected String getSenderEmailHostName() {
        return configuration.getCore().getRuntimeServices().getEmail().getSender().getHostname();
    }

    protected Integer getSenderEmailPort() {
        return configuration.getCore().getRuntimeServices().getEmail().getPort();
    }

    protected Boolean getSenderEmailTlsEnabled() {
        return configuration.getCore().getRuntimeServices().getEmail().getTls().isEnabled();
    }

    protected boolean isThrowExceptionOnFail() {
        return configuration.getCore().getRuntimeServices().getEmail().isThrowExceptionOnFail();
    }

    protected int getSocketTimeout() {
        return configuration.getCore().getRuntimeServices().getEmail().getSocketTimeout();
    }

    protected int getSocketConnectionTimeout() {
        return configuration.getCore().getRuntimeServices().getEmail().getSocketConnectionTimeout();
    }

    protected String getEmailOverrideTo() {
        return configuration.getCore().getRuntimeServices().getEmail().getOverride().getTo();
    }

    protected String getEmailOverrideCc() {
        return configuration.getCore().getRuntimeServices().getEmail().getOverride().getCc();
    }

    protected String getEmailOverrideBcc() {
        return configuration.getCore().getRuntimeServices().getEmail().getOverride().getBcc();
    }

    @Override
    public boolean isConfigured() {
        final String senderEmailAddress = getSenderEmailAddress();
        final String senderEmailPassword = getSenderEmailPassword();
        return !_Strings.isNullOrEmpty(senderEmailAddress) && !_Strings.isNullOrEmpty(senderEmailPassword);
    }

    @Override
    public boolean send(
            final List<String> toList,
            final List<String> ccList,
            final List<String> bccList,
            final String subject,
            final String body,
            final DataSource... attachments) {

        try {
            final ImageHtmlEmail email = new ImageHtmlEmail();

            final String senderEmailUsername = getSenderEmailUsername();
            final String senderEmailAddress = getSenderEmailAddress();
            final String senderEmailPassword = getSenderEmailPassword();
            final String senderEmailHostName = getSenderEmailHostName();
            final Integer senderEmailPort = getSenderEmailPort();
            final Boolean senderEmailTlsEnabled = getSenderEmailTlsEnabled();
            final int socketTimeout = getSocketTimeout();
            final int socketConnectionTimeout = getSocketConnectionTimeout();

            if (senderEmailUsername != null) {
                email.setAuthenticator(new DefaultAuthenticator(senderEmailUsername, senderEmailPassword));
            } else {
                email.setAuthenticator(new DefaultAuthenticator(senderEmailAddress, senderEmailPassword));
            }
            email.setHostName(senderEmailHostName);
            email.setSmtpPort(senderEmailPort);
            email.setStartTLSEnabled(senderEmailTlsEnabled);
            email.setDataSourceResolver(new DataSourceClassPathResolver("/", true));

            email.setSocketTimeout(socketTimeout);
            email.setSocketConnectionTimeout(socketConnectionTimeout);

            final Properties properties = email.getMailSession().getProperties();

            properties.put("mail.smtps.auth", "true");
            properties.put("mail.debug", "true");
            properties.put("mail.smtps.port", "" + senderEmailPort);
            properties.put("mail.smtps.socketFactory.port", "" + senderEmailPort);
            properties.put("mail.smtps.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            properties.put("mail.smtps.socketFactory.fallback", "false");
            properties.put("mail.smtp.starttls.enable", "" + senderEmailTlsEnabled);

            email.setFrom(senderEmailAddress);

            email.setSubject(subject);
            email.setHtmlMsg(body);

            if (attachments != null && attachments.length > 0) {
                for (DataSource attachment : attachments) {
                    email.attach(attachment, attachment.getName(), "");
                }
            }


            final String overrideToList = getEmailOverrideTo();
            final String overrideCc = getEmailOverrideCc();
            final String overrideBcc = getEmailOverrideBcc();

            final String[] toListElseOverride = originalUnlessOverridden(toList, overrideToList);
            if (notEmpty(toListElseOverride)) {
                email.addTo(toListElseOverride);
            }
            final String[] ccListElseOverride = originalUnlessOverridden(ccList, overrideCc);
            if (notEmpty(ccListElseOverride)) {
                email.addCc(ccListElseOverride);
            }
            final String[] bccListElseOverride = originalUnlessOverridden(bccList, overrideBcc);
            if (notEmpty(bccListElseOverride)) {
                email.addBcc(bccListElseOverride);
            }

            email.send();

        } catch (EmailException ex) {
            log.error("An error occurred while trying to send an email", ex);
            final Boolean throwExceptionOnFail = isThrowExceptionOnFail();
            if (throwExceptionOnFail) {
                throw new EmailServiceException(ex);
            }
            return false;
        }

        return true;
    }

    // -- HELPER

    static String[] originalUnlessOverridden(final List<String> original, final String overrideIfAny) {
        final List<String> addresses = _Strings.isNullOrEmpty(overrideIfAny)
                ? original == null
                ? Collections.<String>emptyList()
                        : original
                        : Collections.singletonList(overrideIfAny);
        return addresses.toArray(new String[addresses.size()]);
    }

    static boolean notEmpty(final String[] addresses) {
        return addresses != null && addresses.length > 0;
    }



}
