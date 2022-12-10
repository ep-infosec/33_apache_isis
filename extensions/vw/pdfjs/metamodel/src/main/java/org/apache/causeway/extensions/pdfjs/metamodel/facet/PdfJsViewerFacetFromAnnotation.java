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
package org.apache.causeway.extensions.pdfjs.metamodel.facet;

import java.util.List;

import javax.inject.Inject;

import org.apache.causeway.applib.services.user.UserService;
import org.apache.causeway.core.metamodel.facetapi.FacetHolder;
import org.apache.causeway.extensions.pdfjs.applib.annotations.PdfJsViewer;
import org.apache.causeway.extensions.pdfjs.applib.config.PdfJsConfig;
import org.apache.causeway.extensions.pdfjs.applib.config.Scale;
import org.apache.causeway.extensions.pdfjs.applib.spi.PdfJsViewerAdvisor;

public class PdfJsViewerFacetFromAnnotation extends PdfJsViewerFacetAbstract {

    @Inject List<PdfJsViewerAdvisor> advisors;
    @Inject UserService userService;

    public PdfJsViewerFacetFromAnnotation(final PdfJsConfig config, final FacetHolder holder) {
        super(config, holder);
    }

    public static PdfJsViewerFacetFromAnnotation create(
            final PdfJsViewer annotation,
            final FacetHolder holder) {

        var config = new PdfJsConfig();

        int initialPage = annotation.initialPageNum();
        if (initialPage > 0) {
            config = config.withInitialPage(initialPage);
        }

        final Scale initialScale = annotation.initialScale();
        if (initialScale != Scale._1_00) {
            config = config.withInitialScale(initialScale);
        }

        int initialHeight = annotation.initialHeight();
        if (initialHeight > 0) {
            config = config.withInitialHeight(initialHeight);
        }

        return new PdfJsViewerFacetFromAnnotation(config, holder);
    }

    @Override
    public PdfJsConfig configFor(final PdfJsViewerAdvisor.InstanceKey instanceKey) {
        var config = super.configFor(instanceKey);

        if(advisors != null) {
            for (PdfJsViewerAdvisor advisor : advisors) {
                final PdfJsViewerAdvisor.Advice advice = advisor.advise(instanceKey);
                if(advice != null) {
                    final Integer pageNum = advice.getPageNum();
                    if(pageNum != null) {
                        config = config.withInitialPage(pageNum);
                    }
                    final Scale scale = advice.getScale();
                    if(scale != null) {
                        config = config.withInitialScale(scale);
                    }
                    final Integer height = advice.getHeight();
                    if(height != null) {
                        config = config.withInitialHeight(height);
                    }
                    break;
                }
            }
        }

        return config;
    }

}
