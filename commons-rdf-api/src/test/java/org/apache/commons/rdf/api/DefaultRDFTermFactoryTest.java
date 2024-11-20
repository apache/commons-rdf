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

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Test that all {@link RDFTermFactory} default methods throw
 * {@link UnsupportedOperationException}.
 */
@SuppressWarnings("deprecation")
public class DefaultRDFTermFactoryTest {
    // All methods in RDFTermFactory has a default implementation
    RDFTermFactory factory = new RDFTermFactory() {};

    @Test
    public void createBlankNode() throws Exception {
        assertThrows(UnsupportedOperationException.class, () ->
            factory.createBlankNode());
    }

    @Test
    public void createBlankNodeName() throws Exception {
        assertThrows(UnsupportedOperationException.class, () ->
            factory.createBlankNode("fred"));
    }

    @Test
    public void createGraph() throws Exception {
        assertThrows(UnsupportedOperationException.class, () ->
            factory.createGraph());
    }

    @Test
    public void createIRI() throws Exception {
        assertThrows(UnsupportedOperationException.class, () ->
            factory.createIRI("http://example.com/"));
    }

    @Test
    public void createLiteral() throws Exception {
        assertThrows(UnsupportedOperationException.class, () ->
            factory.createLiteral("Alice"));
    }

    @Test
    public void createLiteralLang() throws Exception {
        assertThrows(UnsupportedOperationException.class, () ->
            factory.createLiteral("Alice", "en"));
    }

    @Test
    public void createLiteralTyped() throws Exception {
        assertThrows(UnsupportedOperationException.class, () ->
            factory.createLiteral("Alice", new DummyIRI(0)));
    }

    @Test
    public void createTriple() throws Exception {
        assertThrows(UnsupportedOperationException.class, () ->
            factory.createTriple(new DummyIRI(1), new DummyIRI(2), new DummyIRI(3)));
    }
}
