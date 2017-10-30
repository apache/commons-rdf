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

public class DummyGraphTest {
    Graph graph = new DummyGraph();

    @Test
    public void add() throws Exception {
        graph.add(new DummyTriple());
    }

    @Test
    public void addSPO() throws Exception {
        graph.add(new DummyIRI(1), new DummyIRI(2), new DummyIRI(3));
    }

    @Test
    public void contains() throws Exception {
        assertTrue(graph.contains(new DummyTriple()));
    }

    @Test
    public void containsSPO() throws Exception {
        assertTrue(graph.contains(null, null, null));
        assertTrue(graph.contains(new DummyIRI(1), new DummyIRI(2), new DummyIRI(3)));
        assertFalse(graph.contains(new DummyIRI(0), new DummyIRI(0), new DummyIRI(0)));
    }

    @Test(expected = IllegalStateException.class)
    public void clearNotSupported() throws Exception {
        graph.clear();
    }

    @Test(expected = IllegalStateException.class)
    public void remove() throws Exception {
        graph.remove(new DummyTriple());
    }

    @Test
    public void removeSPO() throws Exception {
        graph.remove(new DummyIRI(0), new DummyIRI(0), new DummyIRI(0));
    }

    @Test
    public void size() throws Exception {
        assertEquals(1, graph.size());
    }

    @Test
    public void stream() throws Exception {
        assertEquals(new DummyTriple(), graph.stream().findAny().get());
    }

    @Test
    public void streamFiltered() throws Exception {
        assertEquals(new DummyTriple(), graph.stream(null, null, null).findAny().get());
        assertEquals(new DummyTriple(),
                graph.stream(new DummyIRI(1), new DummyIRI(2), new DummyIRI(3)).findAny().get());
        assertFalse(graph.stream(new DummyIRI(0), new DummyIRI(0), new DummyIRI(0)).findAny().isPresent());
    }



}
