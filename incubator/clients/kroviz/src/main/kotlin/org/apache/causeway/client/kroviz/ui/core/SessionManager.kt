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
package org.apache.causeway.client.kroviz.ui.core

import org.apache.causeway.client.kroviz.core.Session
import org.apache.causeway.client.kroviz.core.event.EventStore
import org.apache.causeway.client.kroviz.handler.BaseHandler

/**
 * Handle multiple Sessions
 */
object SessionManager {

    private val sessions = mutableSetOf<Session>()
    private val eventStore = EventStore()
    private var activeSession: Session? = null
    val responseHandlerStatistics = mutableMapOf<String, Int>()

    fun getBaseUrl(): String? {
        return activeSession?.baseUrl
    }

    fun getEventStore(): EventStore {
        return eventStore
    }

    fun login(url: String, username: String, password: String) {
        val menuBar = ViewManager.getRoApp().roMenuBar
        fun updateSession(user: String, session: Session, isFirstSession: Boolean) {
            ViewManager.getRoStatusBar().updateUser(user)
            if (isFirstSession) menuBar.addSeparatorToMainMenu()
            menuBar.add(session)
        }

        val s = Session()
        s.login(url, username, password)
        val isFirstSession = sessions.size == 0
        if (sessions.contains(s)) {
            menuBar.switch(s)
        } else {
            sessions.add(s)
            updateSession(username, s, isFirstSession)
        }
        activeSession = s
    }

    fun getCredentials(): String? {
        return activeSession?.getCredentials()
    }

    fun setApplicationIcon(iconUrl: String) {
        console.log("[SM.setApplicationIcon] $iconUrl")
        activeSession?.resString = iconUrl
        val menuBar = ViewManager.getRoApp().roMenuBar
        menuBar.updateIcon(activeSession!!)
    }

    fun logInvocation(responseHandler: BaseHandler) {
        val className = responseHandler::class.simpleName!!
        val value = responseHandlerStatistics.get(className)
        if (value == null) {
            responseHandlerStatistics.put(className, 1)
        } else {
            responseHandlerStatistics.put(className, value + 1)
        }
    }

}
