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
package org.apache.causeway.client.kroviz.ui.kv.override

/**
 * Copied from Tab in order to:
 * * add IconMenu
 * * recreate svg from LogEntry on focus
 */

import io.kvision.core.Component
import io.kvision.core.ResString
import io.kvision.core.onClick
import io.kvision.html.Icon
import io.kvision.html.Link
import io.kvision.html.TAG
import io.kvision.html.Tag
import io.kvision.panel.TabPanel
import io.kvision.routing.RoutingManager
import io.kvision.snabbdom.VNode
import io.kvision.utils.obj
import org.apache.causeway.client.kroviz.utils.DomUtil
import org.apache.causeway.client.kroviz.utils.ScalableVectorGraphic

/**
 * The single Tab component inside the TabPanel container.
 *
 * @constructor
 * @param label label of the tab
 * @param icon icon of the tab
 * @param image image of the tab
 * @param closable determines if this tab is closable
 * @param route JavaScript route to activate given tab
 * @param init an initializer extension function
 */
@Deprecated("remove when icon menu works with Tab and SVG can be recreated")
open class RoTab(
    label: String? = null, icon: String? = null,
    image: ResString? = null, closable: Boolean = false, val route: String? = null,
    init: (RoTab.() -> Unit)? = null
) : Tag(TAG.LI, className = "nav-item") {

    constructor(
        label: String? = null,
        child: Component,
        icon: String? = null,
        image: ResString? = null,
        closable: Boolean = false,
        route: String? = null,
        init: (RoTab.() -> Unit)? = null
    ) : this(label, icon, image, closable, route, init) {
        @Suppress("LeakingThis")
        add(child)
    }

    var svg: ScalableVectorGraphic? = null

    override fun render(): VNode {
        if (svg != null) DomUtil.appendTo(svg!!)
        return super.render()
    }

    /**
     * The label of the tab.
     */
    var label
        get() = link.label.ifBlank { null }
        set(value) {
            link.label = value ?: ""
        }

    /**
     * The icon of the tab.
     */
    var icon
        get() = link.icon
        set(value) {
            link.icon = value
        }

    /**
     * The image of the tab.
     */
    var image
        get() = link.image
        set(value) {
            link.image = value
        }

    /**
     * Determines if this tab is closable.
     */
    var closable
        get() = closeIcon.visible
        set(value) {
            closeIcon.visible = value
        }

    protected val closeIcon = Icon("fas fa-times").apply {
        addCssClass("kv-tab-close")
        visible = closable
        setEventListener<Icon> {
            click = { e ->
                val tabPanel = (this@RoTab.parent as? RoTabPanel.TabPanelNav)?.tabPanel
                val actIndex = tabPanel?.getTabIndex(this@RoTab) ?: -1
                e.asDynamic().data = actIndex
                @Suppress("UnsafeCastFromDynamic")
                val event = org.w3c.dom.CustomEvent("closingTab", obj { detail = e; cancelable = true })
                if (tabPanel?.getElement()?.dispatchEvent(event) != false) {
                    tabPanel?.removeTab(actIndex)
                    @Suppress("UnsafeCastFromDynamic")
                    val closed = org.w3c.dom.CustomEvent("closedTab", obj { detail = e })
                    tabPanel?.getElement()?.dispatchEvent(closed)
                }
                e.stopPropagation()
            }
        }
    }

    /**
     * A link component within the tab.
     */
    val link = Link(label ?: "", "#", icon, image, className = "nav-link").apply {
        add(this@RoTab.closeIcon)
    }

    internal val tabId = counter++

    protected val routingHandler = { _: Any ->
        (this@RoTab.parent as? RoTabPanel.TabPanelNav)?.tabPanel?.activeTab = this
    }

    init {
        addPrivate(link)
        onClick { e ->
            (this@RoTab.parent as? RoTabPanel.TabPanelNav)?.tabPanel?.activeTab = this
            e.preventDefault()
            if (route != null) {
                RoutingManager.getRouter().kvNavigate(route)
            }
        }
        if (route != null) RoutingManager.getRouter().kvOn(route, routingHandler)
        @Suppress("LeakingThis")
        init?.invoke(this)
    }

    override fun setDragDropData(format: String, data: String) {
        link.setDragDropData(format, data)
    }

    override fun childrenVNodes(): Array<VNode> {
        return (privateChildren!!).filter { it.visible }.map { it.renderVNode() }.toTypedArray()
    }

    override fun dispose() {
        super.dispose()
        if (route != null) RoutingManager.getRouter().kvOff(routingHandler)
    }

    companion object {
        internal var counter = 0
    }
}

/**
 * DSL builder extension function.
 *
 * It takes the same parameters as the constructor of the built component.
 */
fun TabPanel.tab(
    label: String? = null, icon: String? = null,
    image: ResString? = null, closable: Boolean = false, route: String? = null,
    init: (RoTab.() -> Unit)? = null
): RoTab {
    val tab = RoTab(label, icon, image, closable, route, init)
    this.add(tab)
    return tab
}
