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
package demoapp.dom._infra.resources;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.stereotype.Service;

import org.apache.causeway.applib.value.Markup;

import lombok.val;


@Service
@Named("demo.MarkupReaderService")
public class MarkupReaderService {

    public Markup readFor(Class<?> aClass) {
        val markupResourceName = String.format("%s.html", aClass.getSimpleName());
        val html = resourceReaderService.readResource(aClass, markupResourceName);
        return new Markup(html);
    }

    public Markup readFor(Class<?> aClass, final String member) {
        val markupResourceName = String.format("%s-%s.html", aClass.getSimpleName(), member);
        val html = resourceReaderService.readResource(aClass, markupResourceName);
        return new Markup(html);
    }

    @Inject
    ResourceReaderService resourceReaderService;

}
