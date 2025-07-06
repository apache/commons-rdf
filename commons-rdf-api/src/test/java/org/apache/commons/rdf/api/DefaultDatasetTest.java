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

class DefaultDatasetTest {

    DummyDataset dataset = new DummyDataset();

    @Test
    void testClose() throws Exception {
        dataset.close(); // no-op
    }

    @Test
    void testDefaultFilteredIterate() throws Exception {
        assertFalse(dataset.streamCalled);
        assertFalse(dataset.filteredStreamCalled);
        for (final Quad t : dataset.iterate(null, null, new DummyIRI(2), null)) {
            assertEquals(new DummyQuad(), t);
        }
        assertTrue(dataset.filteredStreamCalled);
        assertFalse(dataset.streamCalled);
    }

    @Test
    void testDefaultIterate() throws Exception {
        assertFalse(dataset.streamCalled);
        assertFalse(dataset.filteredStreamCalled);
        for (final Quad t : dataset.iterate()) {
            assertEquals(new DummyQuad(), t);
        }
        assertTrue(dataset.streamCalled);
        assertFalse(dataset.filteredStreamCalled);
    }

}

