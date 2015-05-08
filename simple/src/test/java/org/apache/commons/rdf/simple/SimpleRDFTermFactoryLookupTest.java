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

package org.apache.commons.rdf.simple;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.ServiceLoader;

import org.apache.commons.rdf.api.RDFTermFactory;
import org.junit.Test;

public class SimpleRDFTermFactoryLookupTest {

    @Test
    public void testServiceLoaderLookup() {
        ServiceLoader<RDFTermFactory> loader = ServiceLoader.load(RDFTermFactory.class);

        Iterator<RDFTermFactory> iterator = loader.iterator();
        RDFTermFactory factory = iterator.next();

        assertTrue(factory instanceof SimpleRDFTermFactory);
        assertFalse(iterator.hasNext());
    }
}
