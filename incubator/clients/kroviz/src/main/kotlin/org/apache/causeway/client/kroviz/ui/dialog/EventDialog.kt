/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.causeway.client.kroviz.ui.dialog

import io.kvision.core.CssSize
import io.kvision.core.FlexDirection
import io.kvision.core.UNIT
import io.kvision.panel.VPanel
import org.apache.causeway.client.kroviz.core.event.EventState
import org.apache.causeway.client.kroviz.core.event.ReplayController
import org.apache.causeway.client.kroviz.to.ValueType
import org.apache.causeway.client.kroviz.ui.core.FormItem
import org.apache.causeway.client.kroviz.ui.core.RoDialog
import org.apache.causeway.client.kroviz.ui.core.SessionManager
import org.apache.causeway.client.kroviz.ui.core.ViewManager
import org.apache.causeway.client.kroviz.ui.panel.EventLogTable

class EventDialog(filterState: EventState? = null) : Controller() {

    private val eventPanel = VPanel(spacing = 3) {
        width = CssSize(100, UNIT.perc)
    }
    private var eventTable: EventLogTable

    // callback parameter
    private val EXP: String = "exp"
    private val IMP: String = "imp"
    private val REP: String = "rep"

    init {
        //FIXME actions Pin Export Replay Close
        val customButtons = mutableListOf<FormItem>()
        customButtons.add(FormItem("Export", ValueType.BUTTON, null, callBack = this, callBackAction = EXP))
        customButtons.add(FormItem("Import", ValueType.BUTTON, null, callBack = this, callBackAction = IMP))
        customButtons.add(FormItem("Replay", ValueType.BUTTON, null, callBack = this, callBackAction = REP))

        dialog = RoDialog(
            caption = "Event History",
            items = mutableListOf(),
            controller = this,
            defaultAction = "Pin",
            widthPerc = 60,
            heightPerc = 70,
            customButtons = customButtons
        )
        eventTable = EventLogTable(SessionManager.getEventStore().log, filterState)
        eventTable.tabulator.addCssClass("tabulator-in-dialog")
        eventPanel.add(eventTable)

        val mainPanel = VPanel()
        mainPanel.addCssClass("dialog-content")
        mainPanel.flexDirection = FlexDirection.ROW
        mainPanel.add(eventTable)
        dialog.formPanel!!.add(mainPanel)
    }

    override fun execute(action: String?) {
        when {
            action.isNullOrEmpty() -> {
                ViewManager.add("Event Log", EventLogTable(SessionManager.getEventStore().log))
                dialog.close()
            }
            action == EXP -> {
                EventExportDialog().open()
                dialog.close()
            }
            action == IMP -> {
                EventImportDialog().open()
                dialog.close()
            }
            action == REP -> {
                LoginPrompt(nextController = ReplayController()).open()
                dialog.close()
            }
            else -> {
            }
        }
    }

}
