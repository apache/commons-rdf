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

import org.junit.Test;

/**
 * Test that all {@link RDFTermFactory} default methods throw
 * {@link UnsupportedOperationException}.
 */
@SuppressWarnings("deprecation")
public class DefaultRDFTermFactoryTest {
    // All methods in RDFTermFactory has a default implementation
    RDFTermFactory factory = new RDFTermFactory() {};

    @Test(expected=UnsupportedOperationException.class)
    public void createBlankNode() throws Exception {
        factory.createBlankNode();
    }
    @Test(expected=UnsupportedOperationException.class)
    public void createBlankNodeName() throws Exception {
        factory.createBlankNode("fred");
    }
    @Test(expected=UnsupportedOperationException.class)
    public void createGraph() throws Exception {
        factory.createGraph();
    }
    @Test(expected=UnsupportedOperationException.class)
    public void createIRI() throws Exception {
        factory.createIRI("http://example.com/");
    }
    @Test(expected=UnsupportedOperationException.class)
    public void createLiteral() throws Exception {
        factory.createLiteral("Alice");
    }
    @Test(expected=UnsupportedOperationException.class)
    public void createLiteralLang() throws Exception {
        factory.createLiteral("Alice", "en");
    }
    @Test(expected=UnsupportedOperationException.class)
    public void createLiteralTyped() throws Exception {
        factory.createLiteral("Alice", new DummyIRI(0));
    }
    @Test(expected=UnsupportedOperationException.class)
    public void createTriple() throws Exception {
        factory.createTriple(new DummyIRI(1), new DummyIRI(2), new DummyIRI(3));
    }
}
