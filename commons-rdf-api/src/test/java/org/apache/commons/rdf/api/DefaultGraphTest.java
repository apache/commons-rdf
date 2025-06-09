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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DefaultGraphTest {

    DummyGraph graph = new DummyGraph();

    @Test
    void testClose() throws Exception {
        graph.close(); // no-op
    }

    @Test
    void testDefaultFilteredIterate() throws Exception {
        assertFalse(graph.streamCalled);
        assertFalse(graph.filteredStreamCalled);
        for (final Triple t : graph.iterate(null, new DummyIRI(2), null)) {
            assertEquals(new DummyTriple(), t);
        }
        assertTrue(graph.filteredStreamCalled);
        assertFalse(graph.streamCalled);
    }

    @SuppressWarnings("deprecation")
    @Test
    void testDefaultGetTriples() throws Exception {
        assertFalse(graph.streamCalled);
        assertFalse(graph.filteredStreamCalled);
        assertEquals(1L, graph.getTriples().count());
        assertTrue(graph.streamCalled);
        assertFalse(graph.filteredStreamCalled);
    }

    @SuppressWarnings("deprecation")
    @Test
    void testDefaultGetTriplesFiltered() throws Exception {
        assertFalse(graph.streamCalled);
        assertFalse(graph.filteredStreamCalled);
        assertEquals(1L, graph.getTriples(null,null,null).count());
        assertFalse(graph.streamCalled);
        assertTrue(graph.filteredStreamCalled);
        // Ensure arguments are passed on to graph.stream(s,p,o);
        assertEquals(0L, graph.getTriples(new DummyIRI(0),null,null).count());
    }

    @Test
    void testDefaultIterate() throws Exception {
        assertFalse(graph.streamCalled);
        assertFalse(graph.filteredStreamCalled);
        for (final Triple t : graph.iterate()) {
            assertEquals(new DummyTriple(), t);
        }
        assertTrue(graph.streamCalled);
        assertFalse(graph.filteredStreamCalled);
    }

}

