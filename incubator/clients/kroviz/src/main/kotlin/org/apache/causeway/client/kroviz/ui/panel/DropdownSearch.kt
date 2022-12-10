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
package org.apache.causeway.client.kroviz.ui.panel

import io.kvision.form.formPanel
import io.kvision.form.select.AjaxOptions
import io.kvision.form.select.Select
import io.kvision.panel.SimplePanel
import io.kvision.utils.obj
import io.kvision.utils.pc
import io.kvision.utils.px
import kotlinx.serialization.Serializable

@Serializable
data class Form(
        val select: String? = null,
        val ajaxselect: String? = null,
)

class DropdownSearch() : SimplePanel() {
    val cachedValues = listOf("1" to "About", "2" to "Base", "3" to "Blog", "4" to "Contact", "5" to "Custom", "6" to "Support", "7" to "Tools")

    init {
        this.marginTop = 10.px
        this.marginLeft = 40.px
        this.width = 100.pc
        formPanel<Form> {
            add(
                    Form::select, Select(
                    options = cachedValues,
                    label = "Dropdown search with in-memory values"
            ).apply {
                liveSearch = true
            }
            )
            add(Form::ajaxselect, Select(label = "Dropdown search on remote data source").apply {
                emptyOption = true
                ajaxOptions = AjaxOptions("https://api.github.com/search/repositories", preprocessData = {
                    it.items.map { item ->
                        obj {
                            this.value = item.id
                            this.text = item.name
                            this.data = obj {
                                this.subtext = item.owner.login
                            }
                        }
                    }
                }, data = obj {
                    q = "{{{q}}}"
                }, minLength = 3, requestDelay = 500)
            })
        }
    }
}
