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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests for the {@link org.apache.commons.rdf.simple.Types} enumeration.
 */
public class TypesTest {

    /**
     * Test method for
     * {@link org.apache.commons.rdf.simple.Types#getIRIString()} .
     */
    @Test
    public final void testGetIRIString() {
        assertEquals("http://www.w3.org/1999/02/22-rdf-syntax-ns#langString", Types.RDF_LANGSTRING.getIRIString());
    }

    /**
     * Test method for
     * {@link org.apache.commons.rdf.simple.Types#ntriplesString()}.
     */
    @Test
    public final void testNtriplesString() {
        assertEquals("<http://www.w3.org/1999/02/22-rdf-syntax-ns#langString>", Types.RDF_LANGSTRING.ntriplesString());
    }

    /**
     * Test method for
     * {@link org.apache.commons.rdf.simple.Types#get(org.apache.commons.rdf.api.IRI)}
     * .
     */
    @Test
    public final void testGet() {
        assertTrue(Types.get(new IRIImpl("http://www.w3.org/2001/XMLSchema#boolean")).isPresent());
        assertEquals("http://www.w3.org/2001/XMLSchema#boolean",
                Types.get(new IRIImpl("http://www.w3.org/2001/XMLSchema#boolean")).get().getIRIString());
        assertFalse(Types.get(new IRIImpl("http://www.w3.org/2001/XMLSchema#nonExistent")).isPresent());
    }

}
