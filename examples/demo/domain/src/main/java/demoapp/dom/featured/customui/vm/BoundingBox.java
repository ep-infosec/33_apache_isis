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

import demoapp.dom.featured.customui.latlng.LatLng;
import lombok.Data;
import lombok.Getter;

@Data
public class BoundingBox {
    @Getter
    private final LatLng minimum;
    @Getter
    private final LatLng maximum;

    public String getMinimumLatitude() {
        return minimum.getLatitude();
    }

    public String getMinimumLongitude() {
        return minimum.getLongitude();
    }

    public String getMaximumLatitude() {
        return maximum.getLatitude();
    }

    public String getMaximumLongitude() {
        return maximum.getLongitude();
    }

    public final String toUrl(String divider) {
        return getMinimumLongitude() + divider + this.getMinimumLatitude() + divider + getMaximumLongitude() + divider + getMaximumLatitude();
    }

    public final String toUrl() {
        return toUrl("%2C");
    }
}
