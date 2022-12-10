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
package org.apache.causeway.viewer.restfulobjects.rendering.util;

import java.util.List;

import org.apache.causeway.commons.internal.collections._Lists;
import org.apache.causeway.viewer.restfulobjects.applib.util.PathNode;

public final class FollowSpecUtil {

    private FollowSpecUtil() {
    }

    public static final List<List<PathNode>> asFollowSpecs(final List<List<String>> links) {

        final List<List<PathNode>> unmodifiable = _Lists.map(links, (List<String> pathParts) -> {
            return _Lists.newArrayList(_Lists.map(pathParts, (String input)->PathNode.parse(input)));
        });

        return _Lists.newArrayList(unmodifiable);
    }
}
