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
package demoapp.dom.featured.customui.vm;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.causeway.applib.annotation.Action;
import org.apache.causeway.applib.annotation.ActionLayout;
import org.apache.causeway.applib.annotation.DomainService;
import org.apache.causeway.applib.annotation.MemberSupport;
import org.apache.causeway.applib.annotation.NatureOfService;
import org.apache.causeway.applib.annotation.PriorityPrecedence;
import org.apache.causeway.applib.annotation.SemanticsOf;

import demoapp.dom.featured.customui.geocoding.GeoapifyClient;
import demoapp.dom.featured.customui.latlng.Zoom;
import lombok.val;

@Named("demo.WhereInTheWorldMenu")
@DomainService(
        nature=NatureOfService.VIEW
)
@javax.annotation.Priority(PriorityPrecedence.EARLY)
public class WhereInTheWorldMenu {

//tag::action[]
    @Inject
    private GeoapifyClient geoapifyClient;

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(
            cssClassFa="fa-globe",
            describedAs="Opens a Custom UI page displaying a map for the provided address"
    )
    public WhereInTheWorldVm whereInTheWorld(
            final String address,
            @Zoom final int zoom) {
        val vm = new WhereInTheWorldVm();

        val latLng = geoapifyClient.geocode(address);
        vm.setAddress(address);
        vm.setLatitude(latLng.getLatitude());
        vm.setLongitude(latLng.getLongitude());
        vm.setZoom(zoom);

        return vm;
    }
//end::action[]
    @MemberSupport public List<String> choices0WhereInTheWorld() {
        return Arrays.asList("Malvern, UK", "Vienna, Austria", "Leeuwarden, Netherlands", "Dublin, Ireland");
    }

    @MemberSupport public String default0WhereInTheWorld() {
        return "Malvern, UK";
    }

    @MemberSupport public int default1WhereInTheWorld() {
        return 14;
    }

}
