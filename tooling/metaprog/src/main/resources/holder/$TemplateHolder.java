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
package /*${java-package}*/;

import javax.inject.Named;

/*${generated-file-notice}*/
@Named("demo./*${showcase-name}*/Holder")
//tag::class[]
public interface /*${showcase-name}*/Holder {

    /*${showcase-type}*/ getReadOnlyProperty();
    void setReadOnlyProperty(/*${showcase-type}*/ c);

    /*${showcase-type}*/ getReadWriteProperty();
    void setReadWriteProperty(/*${showcase-type}*/ c);

    /*${showcase-type}*/ getReadOnlyOptionalProperty();
    void setReadOnlyOptionalProperty(/*${showcase-type}*/ c);

    /*${showcase-type}*/ getReadWriteOptionalProperty();
    void setReadWriteOptionalProperty(/*${showcase-type}*/ c);

}
//end::class[]
