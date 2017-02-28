/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.rdf.api.fluentwriter;

import java.io.OutputStream;
import java.nio.file.Path;

import org.apache.commons.rdf.api.io.Option;
import org.apache.commons.rdf.api.io.WriterTarget;

public interface NeedTarget extends _Buildable {
    @Override
    NeedTarget build();
    <V> NeedTarget option(Option<V> option, V value);
    
    NeedSource target(Path p);
    NeedSource target(OutputStream out);
    NeedSource target(WriterTarget target);
}
