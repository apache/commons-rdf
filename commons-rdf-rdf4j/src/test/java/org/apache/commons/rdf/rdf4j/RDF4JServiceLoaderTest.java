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

package org.apache.commons.rdf.rdf4j;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.ServiceLoader;

import org.apache.commons.rdf.api.RDF;
import org.junit.jupiter.api.Test;

class RDF4JServiceLoaderTest {

    @Test
    void testServiceLoaderLookup() {
        final ServiceLoader<RDF> loader = ServiceLoader.load(RDF.class);
        for (final RDF impl : loader) {
            if (impl instanceof RDF4J) {
                return; // yay
            }
        }
        fail("RDF4J not found in ServiceLoader");
    }
}
