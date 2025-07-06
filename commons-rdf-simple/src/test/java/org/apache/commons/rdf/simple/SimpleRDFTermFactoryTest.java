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

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.apache.commons.rdf.api.BlankNode;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Literal;
import org.apache.commons.rdf.api.Triple;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link SimpleRDFTermFactory}.
 */
class SimpleRDFTermFactoryTest {

    @Test
    void testCreateBlankNode() {
        final SimpleRDFTermFactory factory = new SimpleRDFTermFactory();
        final BlankNode actual = factory.createBlankNode();
        assertInstanceOf(BlankNodeImpl.class, actual);
    }

    @Test
    void testCreateBlankNodeWithName() {
        final SimpleRDFTermFactory factory = new SimpleRDFTermFactory();
        final BlankNode actual = factory.createBlankNode("name");
        assertInstanceOf(BlankNodeImpl.class, actual);
    }

    @Test
    void testCreateGraph() {
        final SimpleRDFTermFactory factory = new SimpleRDFTermFactory();
        final Graph actual = factory.createGraph();
        assertInstanceOf(GraphImpl.class, actual);
    }

    @Test
    void testCreateIRIWithIRI() {
        final SimpleRDFTermFactory factory = new SimpleRDFTermFactory();
        final IRI actual = factory.createIRI(Types.RDF_LANGSTRING.getIRIString());
        assertInstanceOf(Types.class, actual);
    }

    @Test
    void testCreateLiteralWithLiteral() {
        final SimpleRDFTermFactory factory = new SimpleRDFTermFactory();
        final Literal actual = factory.createLiteral("literal");
        assertInstanceOf(LiteralImpl.class, actual);
    }

    @Test
    void testCreateLiteralWithLiteralAndIRI() {
        final SimpleRDFTermFactory factory = new SimpleRDFTermFactory();
        final Literal actual = factory.createLiteral("literal", Types.RDF_PLAINLITERAL);
        assertInstanceOf(LiteralImpl.class, actual);
    }

    @Test
    void testCreateLiteralWithLiteralAndLanguage() {
        final SimpleRDFTermFactory factory = new SimpleRDFTermFactory();
        final Literal actual = factory.createLiteral("literal", "language");
        assertInstanceOf(LiteralImpl.class, actual);
    }

    @Test
    void testCreateTriple() {
        final SimpleRDFTermFactory factory = new SimpleRDFTermFactory();
        final Triple actual = factory.createTriple(Types.RDF_PLAINLITERAL, Types.RDF_LANGSTRING, factory.createLiteral("literal"));
        assertInstanceOf(TripleImpl.class, actual);
    }
}
