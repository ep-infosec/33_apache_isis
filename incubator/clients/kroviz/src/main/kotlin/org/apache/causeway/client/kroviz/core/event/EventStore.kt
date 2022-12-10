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
package org.apache.causeway.client.kroviz.core.event

import io.kvision.panel.SimplePanel
import io.kvision.state.observableListOf
import org.apache.causeway.client.kroviz.core.aggregator.BaseAggregator
import org.apache.causeway.client.kroviz.core.aggregator.SvgDispatcher
import org.apache.causeway.client.kroviz.to.TObject
import org.apache.causeway.client.kroviz.to.mb.Menubars
import org.apache.causeway.client.kroviz.ui.core.SessionManager
import org.apache.causeway.client.kroviz.ui.core.ViewManager
import org.apache.causeway.client.kroviz.utils.StringUtils
import org.apache.causeway.client.kroviz.utils.UUID
import org.w3c.files.Blob

/**
 * Keeps a log of remote invocations and the responses.
 * Subsequent invocations are served from this cache.
 * UI events (Dialogs, Windows, etc.) are logged here as well.
 *
 * @see "https://en.wikipedia.org/wiki/Proxy_pattern"
 * @see "https://martinfowler.com/eaaDev/EventSourcing.html"
 */
class EventStore {
    var log = observableListOf<LogEntry>()

    private fun log(logEntry: LogEntry) {
        log.add(logEntry)
    }

    fun getLogStartMilliSeconds(): Double {
        return if (log.size >= 1) {
            val le = log[0]
            le.createdAt.getTime()
        } else {
            0.toDouble()
        }
    }

    fun start(
        rs: ResourceSpecification,
        method: String,
        body: String = "",
        aggregator: BaseAggregator? = null,
    ): LogEntry {
        val entry = LogEntry(rs = rs, method = method, request = body)
        if (aggregator != null) {
            entry.addAggregator(aggregator)
        }
        entry.runningAtStart = countRunning()
        log(entry)
        updateStatus(entry)
        return entry
    }

    fun add(reSpec: ResourceSpecification) {
        val entry = LogEntry(reSpec)
        log(entry)
        updateStatus(entry)
    }

    fun addView(title: String, aggregator: BaseAggregator, panel: SimplePanel) {
        val entry = LogEntry(title = title, aggregator = aggregator)
        entry.panel = panel
        log(entry)
        updateStatus(entry)
    }

    fun closeView(title: String) {
        val logEntry = findView(title)
        if (null != logEntry) {
            logEntry.setClose()
            logEntry.getAggregator()?.reset()
            updateStatus(logEntry)
        }
    }

    fun addUserAction(aggregator: BaseAggregator, obj: TObject) {
        val entry = LogEntry(title = obj.title, aggregator = aggregator)
        entry.obj = obj
        entry.state = EventState.USER_ACTION
        log(entry)
    }

    fun end(reSpec: ResourceSpecification, response: String): LogEntry? {
        val entry: LogEntry? = findBy(reSpec)
        if (entry != null) {
            entry.response = response
            entry.setSuccess()
            entry.runningAtEnd = countRunning()
            updateStatus(entry)
        }
        return entry
    }

    fun end(reSpec: ResourceSpecification, pumlCode: String, response: Any?): LogEntry? {
        val entry: LogEntry? = findBy(reSpec, pumlCode)
        val credentials: String = SessionManager.getCredentials()!!

        if (entry != null) {
            when (response) {
                is String -> {
                    if (response.isEmpty()) {
                        console.log("[ES.end]")
                        console.log(reSpec)
                        val rebound = CorsHttpRequest().invoke(reSpec.url, credentials)
                        if (rebound.isEmpty()) {
                            throw IllegalStateException("CORS issue while accessing layout xml")
                        } else {
                            entry.response = rebound
                        }
                    } else {
                        entry.response = response
                    }
                }
                is Blob -> entry.blob = response
                else -> {
                }
            }
            entry.setSuccess()
            entry.runningAtEnd = countRunning()
            updateStatus(entry)
        }
        return entry
    }

    fun fault(reSpec: ResourceSpecification, fault: String) {
        val entry: LogEntry? = findBy(reSpec)
        entry!!.setError(fault)
        entry.runningAtEnd = countRunning()
        updateStatus(entry)
    }

    internal fun updateStatus(entry: LogEntry) {
        val successCnt = log.count { le -> le.isSuccess() }
        val runningCnt = countRunning()
        val errorCnt = log.count { le -> le.isError() }
        val viewCnt = log.count { le -> le.isView() }
        val status = StatusPo(successCnt, runningCnt, errorCnt, viewCnt, 0)
        ViewManager.updateStatus(status)
    }

    private fun countRunning(): Int {
        return log.count { le -> le.isRunning() }
    }

    /**
     * Answers the first matching entry.
     */
    fun findBy(rs: ResourceSpecification): LogEntry? {
        return if (rs.isRedundant()) {
            findEquivalent(rs)
        } else {
            findExact(rs)
        }
    }

    fun findBy(rs: ResourceSpecification, body: String): LogEntry? {
        return log.firstOrNull() {
            it.url == rs.url
                    && it.subType == rs.subType
                    && it.request == body
        }
    }

    fun findBy(tObject: TObject): LogEntry? {
        return log.firstOrNull() {
            it.obj is TObject && (it.obj as TObject).instanceId == tObject.instanceId
        }
    }

    fun findBy(shortTitle: String): LogEntry? {
        return log.firstOrNull { StringUtils.shortTitle(it.title) == shortTitle }
    }

    fun findBy(aggregator: BaseAggregator): LogEntry? {
        return log.firstOrNull { it.getAggregator() == aggregator }
    }

    fun findAllBy(aggregator: BaseAggregator): List<LogEntry> {
        return log.filter { it.getAggregator() == aggregator }
    }

    fun findByDispatcher(uuid: UUID): LogEntry {
        return log.first {
            val aggt = it.getAggregator()
            aggt is SvgDispatcher
                    && aggt.callBack == uuid
        }
    }

    fun findMenuBars(): LogEntry? {
        return log.firstOrNull() {
            it.obj is Menubars
        }
    }

    fun findMenuBarsBy(baseUrl: String): LogEntry? {
        return log.firstOrNull() {
            it.obj is Menubars && it.url.startsWith(baseUrl)
        }
    }

    fun findAll(reSpec: ResourceSpecification): List<LogEntry> {
        return log.filter {
            it.matches(reSpec)
        }
    }


    //public for test
    fun findExact(reSpec: ResourceSpecification): LogEntry? {
        return log.firstOrNull {
            it.matches(reSpec)
        }
    }

    //public for test
    fun findView(title: String): LogEntry? {
        return log.firstOrNull {
            it.title == title && it.isView()
        }
    }

    //public for test
    fun findEquivalent(reSpec: ResourceSpecification): LogEntry? {
        return log.firstOrNull {
            reSpec.matches(it)
        }
    }

    fun reset() {
        log.removeAll(log)
    }

}
