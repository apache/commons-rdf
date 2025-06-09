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

package org.apache.commons.rdf.simple.experimental;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.apache.commons.rdf.experimental.RDFParser;
import org.apache.commons.rdf.simple.DummyRDFParserBuilder;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link RDFParseException}.
 */
class RDFParseExceptionTest {

    RDFParser rdfParser = new DummyRDFParserBuilder();

    @Test
    void testGetCauseAndMessage() {
        final IOException e = new IOException();
        e.fillInStackTrace();
        assertEquals(null, new RDFParseException(rdfParser).getCause());
        assertEquals(null, new RDFParseException(rdfParser, (String) null).getCause());
        assertEquals("a", new RDFParseException(rdfParser, "a", e).getMessage());
        assertEquals(e, new RDFParseException(rdfParser, "a", e).getCause());
    }

    @Test
    void testGetCauseNoMessage() {
        final IOException e = new IOException();
        e.fillInStackTrace();
        assertEquals(null, new RDFParseException(rdfParser).getCause());
        assertEquals(e.getClass().getName(), new RDFParseException(rdfParser, e).getMessage());
        assertEquals(e, new RDFParseException(rdfParser, e).getCause());
    }

    @Test
    void testGetMessage() {
        assertEquals(null, new RDFParseException(rdfParser).getMessage());
        assertEquals(null, new RDFParseException(rdfParser, (String) null).getMessage());
        assertEquals("a", new RDFParseException(rdfParser, "a").getMessage());
    }

    @Test
    void testGetRDFParser() {
        assertEquals(rdfParser, new RDFParseException(rdfParser).getRDFParserBuilder());
    }
}
