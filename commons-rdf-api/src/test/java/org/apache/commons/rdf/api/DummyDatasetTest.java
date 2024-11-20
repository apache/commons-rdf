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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class DummyDatasetTest {
    Dataset dataset = new DummyDataset();

    @Test
    public void clearNotSupported() throws Exception {
        assertThrows(IllegalStateException.class, () ->
            dataset.clear());
    }

    @Test
    public void remove() throws Exception {
        assertThrows(IllegalStateException.class, () ->
            dataset.remove(new DummyQuad()));
    }

    @Test
    public void testAdd() throws Exception {
        dataset.add(new DummyQuad());
    }

    @Test
    public void testAddSPO() throws Exception {
        dataset.add(null, new DummyIRI(1), new DummyIRI(2), new DummyIRI(3));
    }

    @Test
    public void testContains() throws Exception {
        assertTrue(dataset.contains(new DummyQuad()));
    }

    @Test
    public void testContainsSPO() throws Exception {
        assertTrue(dataset.contains(null, null, null, null));
        assertTrue(dataset.contains(null, new DummyIRI(1), new DummyIRI(2), new DummyIRI(3)));
        assertFalse(dataset.contains(null, new DummyIRI(0), new DummyIRI(0), new DummyIRI(0)));
    }

    @Test
    public void testGetGraph() throws Exception {
        assertInstanceOf(DummyGraph.class, dataset.getGraph());
    }

    @Test
    public void testGetGraphNamed() throws Exception {
        assertFalse(dataset.getGraph(new DummyIRI(0)).isPresent());
    }

    @Test
    public void testGetGraphNames() throws Exception {
        assertFalse(dataset.getGraphNames().findAny().isPresent());
    }

    @Test
    public void testGetGraphNull() throws Exception {
        assertInstanceOf(DummyGraph.class, dataset.getGraph(null).get());
    }

    @Test
    public void testRemoveSPO() throws Exception {
        dataset.remove(null, new DummyIRI(0), new DummyIRI(0), new DummyIRI(0));
    }

    @Test
    public void testSize() throws Exception {
        assertEquals(1, dataset.size());
    }

    @Test
    public void testStream() throws Exception {
        assertEquals(new DummyQuad(), dataset.stream().findAny().get());
    }

    @Test
    public void testStreamFiltered() throws Exception {
        assertEquals(new DummyQuad(), dataset.stream(null, null, null, null).findAny().get());
        assertEquals(new DummyQuad(),
                dataset.stream(null, new DummyIRI(1), new DummyIRI(2), new DummyIRI(3)).findAny().get());
        assertFalse(dataset.stream(null, new DummyIRI(0), new DummyIRI(0), new DummyIRI(0)).findAny().isPresent());
    }
}
