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

import java.util.Objects;

import org.junit.jupiter.api.Test;

class DummyQuadTest {
    Quad quad = new DummyQuad();

    @Test
    void testEquals() throws Exception {
        assertEquals(new DummyQuad(), quad);
    }

    @Test
    void testGetGraphName() throws Exception {
        assertFalse(quad.getGraphName().isPresent());
    }

    @Test
    void testGetObject() throws Exception {
        assertEquals(3, ((DummyIRI) quad.getObject()).i);
    }

    @Test
    void testGetPredicate() throws Exception {
        assertEquals(2, ((DummyIRI) quad.getPredicate()).i);
    }

    @Test
    void testGetSubject() throws Exception {
        assertEquals(1, ((DummyIRI) quad.getSubject()).i);
    }

    @Test
    void testHashCode() {
        final int expected = Objects.hash(quad.getSubject(), quad.getPredicate(), quad.getObject(), quad.getGraphName());
        assertEquals(expected, quad.hashCode());
    }
}

