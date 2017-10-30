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

import static org.junit.Assert.*;

import org.junit.Test;

public class DefaultGraphTest {

    DummyGraph graph = new DummyGraph();

    @Test
    public void close() throws Exception {
        graph.close(); // no-op
    }

    @SuppressWarnings("deprecation")
    @Test
    public void defaultGetTriples() throws Exception {
        assertFalse(graph.streamCalled);
        assertFalse(graph.filteredStreamCalled);
        assertEquals(1L, graph.getTriples().count());
        assertTrue(graph.streamCalled);
        assertFalse(graph.filteredStreamCalled);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void defaultGetTriplesFiltered() throws Exception {
        assertFalse(graph.streamCalled);
        assertFalse(graph.filteredStreamCalled);
        assertEquals(1L, graph.getTriples(null,null,null).count());
        assertFalse(graph.streamCalled);
        assertTrue(graph.filteredStreamCalled);
        // Ensure arguments are passed on to graph.stream(s,p,o);
        assertEquals(0L, graph.getTriples(new DummyIRI(0),null,null).count());
    }

    @Test
    public void defaultIterate() throws Exception {
        assertFalse(graph.streamCalled);
        assertFalse(graph.filteredStreamCalled);
        for (final Triple t : graph.iterate()) {
            assertEquals(t, new DummyTriple());
        }
        assertTrue(graph.streamCalled);
        assertFalse(graph.filteredStreamCalled);
    }

    @Test
    public void defaultFilteredIterate() throws Exception {
        assertFalse(graph.streamCalled);
        assertFalse(graph.filteredStreamCalled);
        for (final Triple t : graph.iterate(null, new DummyIRI(2), null)) {
            assertEquals(t, new DummyTriple());
        }
        assertTrue(graph.filteredStreamCalled);
        assertFalse(graph.streamCalled);
    }

}

