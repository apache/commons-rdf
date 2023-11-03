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
package org.apache.commons.rdf.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class DummyIRITest {
    DummyIRI iri = new DummyIRI(1337);

    @Test
    public void testEquals() throws Exception {
        assertEquals(iri, new DummyIRI(1337));
    }

    @Test
    public void testGetIRIString() throws Exception {
        assertEquals("http://example.com/1337", iri.getIRIString());
    }

    @Test
    public void testHashCode() throws Exception {
        assertEquals("http://example.com/1337".hashCode(), iri.hashCode());
    }

    @Test
    public void testI() throws Exception {
        assertEquals(1337, iri.i);
    }

    @Test
    public void testNotEquals() throws Exception {
        assertNotEquals(iri, new DummyIRI(1));
    }

    @Test
    public void testNtriplesString() throws Exception {
        assertEquals("<http://example.com/1337>", iri.ntriplesString());
    }
}
