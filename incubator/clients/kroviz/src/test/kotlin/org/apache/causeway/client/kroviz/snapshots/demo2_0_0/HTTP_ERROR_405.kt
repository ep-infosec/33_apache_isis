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
package org.apache.causeway.client.kroviz.snapshots.demo2_0_0

import org.apache.causeway.client.kroviz.snapshots.Response

object HTTP_ERROR_405: Response() {
    val invoke = "\$invoke"
    val invokeOnTarget = "\$invokeOnTarget"
    val preprocess = "\$preprocess"
    val doFilter = "\$doFilter"
    val ConnectionHandler = "\$ConnectionHandler"
    val SocketProcessor = "\$SocketProcessor"
    val Worker = "\$Worker"
    val WrappingRunnable = "\$WrappingRunnable"

    override val url = ""
    override val str = """{
    "httpStatusCode": 405,
    "message": "Method not allowed; action 'blobs' does not have safe semantics",
    "detail": {
        "className": "org.apache.causeway.viewer.restfulobjects.rendering.RestfulObjectsApplicationException",
        "message": "Method not allowed; action 'blobs' does not have safe semantics",
        "element": [
            "org.apache.causeway.viewer.restfulobjects.rendering.RestfulObjectsApplicationException.createWithCauseAndMessage(RestfulObjectsApplicationException.java:50)",
            "org.apache.causeway.viewer.restfulobjects.rendering.RestfulObjectsApplicationException.createWithMessage(RestfulObjectsApplicationException.java:37)",
            "org.apache.causeway.viewer.restfulobjects.viewer.resources.DomainResourceHelper.invokeActionQueryOnly(DomainResourceHelper.java:169)",
            "org.apache.causeway.viewer.restfulobjects.viewer.resources.DomainObjectResourceServerside.invokeActionQueryOnly(DomainObjectResourceServerside.java:680)",
            "sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)",
            "sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)",
            "sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)",
            "java.lang.reflect.Method.invoke(Method.java:498)",
            "org.jboss.resteasy.core.MethodInjectorImpl.invoke(MethodInjectorImpl.java:151)",
            "org.jboss.resteasy.core.MethodInjectorImpl.lambda$invoke${'$'}3(MethodInjectorImpl.java:122)",
            "java.util.concurrent.CompletableFuture.uniApply(CompletableFuture.java:602)",
            "java.util.concurrent.CompletableFuture.uniApplyStage(CompletableFuture.java:614)",
            "java.util.concurrent.CompletableFuture.thenApply(CompletableFuture.java:1983)",
            "java.util.concurrent.CompletableFuture.thenApply(CompletableFuture.java:110)",
            "org.jboss.resteasy.core.MethodInjectorImpl.invoke(MethodInjectorImpl.java:122)",
            "org.jboss.resteasy.core.ResourceMethodInvoker.internalInvokeOnTarget(ResourceMethodInvoker.java:594)",
            "org.jboss.resteasy.core.ResourceMethodInvoker.invokeOnTargetAfterFilter(ResourceMethodInvoker.java:468)",
            "org.jboss.resteasy.core.ResourceMethodInvoker.lambda$invokeOnTarget${'$'}2(ResourceMethodInvoker.java:421)",
            "org.jboss.resteasy.core.interception.jaxrs.PreMatchContainerRequestContext.filter(PreMatchContainerRequestContext.java:363)",
            "org.jboss.resteasy.core.ResourceMethodInvoker.invokeOnTarget(ResourceMethodInvoker.java:423)",
            "org.jboss.resteasy.core.ResourceMethodInvoker.invoke(ResourceMethodInvoker.java:391)",
            "org.jboss.resteasy.core.ResourceMethodInvoker.lambda$invoke${'$'}1(ResourceMethodInvoker.java:365)",
            "java.util.concurrent.CompletableFuture.uniComposeStage(CompletableFuture.java:981)",
            "java.util.concurrent.CompletableFuture.thenCompose(CompletableFuture.java:2124)",
            "java.util.concurrent.CompletableFuture.thenCompose(CompletableFuture.java:110)",
            "org.jboss.resteasy.core.ResourceMethodInvoker.invoke(ResourceMethodInvoker.java:365)",
            "org.jboss.resteasy.core.SynchronousDispatcher.invoke(SynchronousDispatcher.java:477)",
            "org.jboss.resteasy.core.SynchronousDispatcher.lambda$invoke${'$'}4(SynchronousDispatcher.java:252)",
            "org.jboss.resteasy.core.SynchronousDispatcher.lambda$preprocess${'$'}0(SynchronousDispatcher.java:153)",
            "org.jboss.resteasy.core.interception.jaxrs.PreMatchContainerRequestContext.filter(PreMatchContainerRequestContext.java:363)",
            "org.jboss.resteasy.core.SynchronousDispatcher.preprocess(SynchronousDispatcher.java:156)",
            "org.jboss.resteasy.core.SynchronousDispatcher.invoke(SynchronousDispatcher.java:238)",
            "org.jboss.resteasy.plugins.server.servlet.ServletContainerDispatcher.service(ServletContainerDispatcher.java:249)",
            "org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher.service(HttpServletDispatcher.java:60)",
            "org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher.service(HttpServletDispatcher.java:55)",
            "javax.servlet.http.HttpServlet.service(HttpServlet.java:741)",
            "org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:231)",
            "org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)",
            "org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:53)",
            "org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)",
            "org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)",
            "org.apache.causeway.viewer.restfulobjects.viewer.webmodule.CausewayTransactionFilterForRestfulObjects.lambda$doFilter${'$'}0(CausewayTransactionFilterForRestfulObjects.java:48)",
            "org.apache.causeway.core.runtimeservices.xactn.TransactionServiceSpring${'$'}1.doInTransactionWithoutResult(TransactionServiceSpring.java:113)",
            "org.springframework.transaction.support.TransactionCallbackWithoutResult.doInTransaction(TransactionCallbackWithoutResult.java:36)",
            "org.springframework.transaction.support.TransactionTemplate.execute(TransactionTemplate.java:140)",
            "org.apache.causeway.core.runtimeservices.xactn.TransactionServiceSpring.executeWithinNewTransaction(TransactionServiceSpring.java:109)",
            "org.apache.causeway.core.runtimeservices.xactn.TransactionServiceSpring.executeWithinTransaction(TransactionServiceSpring.java:140)",
            "org.apache.causeway.viewer.restfulobjects.viewer.webmodule.CausewayTransactionFilterForRestfulObjects.doFilter(CausewayTransactionFilterForRestfulObjects.java:45)",
            "org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)",
            "org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)",
            "org.apache.causeway.viewer.restfulobjects.viewer.webmodule.CausewayRestfulObjectsSessionFilter.doFilter(CausewayRestfulObjectsSessionFilter.java:379)",
            "org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)",
            "org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)",
            "org.apache.causeway.core.webapp.diagnostics.CausewayLogOnExceptionFilter.doFilter(CausewayLogOnExceptionFilter.java:59)",
            "org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)",
            "org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)",
            "org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100)",
            "org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)",
            "org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)",
            "org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)",
            "org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93)",
            "org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)",
            "org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)",
            "org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)",
            "org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201)",
            "org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)",
            "org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)",
            "org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)",
            "org.ebaysf.web.cors.CORSFilter.handleNonCORS(CORSFilter.java:437)",
            "org.ebaysf.web.cors.CORSFilter.doFilter(CORSFilter.java:172)",
            "org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)",
            "org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)",
            "org.apache.shiro.web.servlet.AbstractShiroFilter.executeChain(AbstractShiroFilter.java:449)",
            "org.apache.shiro.web.servlet.AbstractShiroFilter${'$'}1.call(AbstractShiroFilter.java:365)",
            "org.apache.shiro.subject.support.SubjectCallable.doCall(SubjectCallable.java:90)",
            "org.apache.shiro.subject.support.SubjectCallable.call(SubjectCallable.java:83)",
            "org.apache.shiro.subject.support.DelegatingSubject.execute(DelegatingSubject.java:387)",
            "org.apache.shiro.web.servlet.AbstractShiroFilter.doFilterInternal(AbstractShiroFilter.java:362)",
            "org.apache.shiro.web.servlet.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:125)",
            "org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)",
            "org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)",
            "org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:202)",
            "org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:96)",
            "org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:526)",
            "org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:139)",
            "org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:92)",
            "org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:74)",
            "org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:343)",
            "org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:367)",
            "org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:65)",
            "org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:860)",
            "org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1591)",
            "org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:49)",
            "java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)",
            "java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)",
            "org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61)",
            "java.lang.Thread.run(Thread.java:748)"
        ],
        "causedBy": null
    }
}
"""
}
