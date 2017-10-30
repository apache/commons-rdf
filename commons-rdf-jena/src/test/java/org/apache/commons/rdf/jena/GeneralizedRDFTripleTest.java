/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
package org.apache.commons.rdf.jena;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.rdf.api.BlankNode;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.junit.Test;

public class GeneralizedRDFTripleTest {

    private final JenaRDF jena = new JenaRDF();

    @Test
    public void bnodeProperty() throws Exception {
        final BlankNode b1 = jena.createBlankNode("b1");
        final JenaIRI ex1 = jena.createIRI("http://example.com/ex1");
        final JenaIRI ex2 = jena.createIRI("http://example.com/ex2");

        final JenaGeneralizedTripleLike t = jena.createGeneralizedTriple(ex1, b1, ex2);
        assertEquals(ex1, t.getSubject());
        assertEquals(ex2, t.getObject());
        assertEquals(b1, t.getPredicate()); // it's a bnode!
        assertTrue(t.asJenaTriple().getPredicate().isBlank());
    }

    @Test
    public void literalPredicate() throws Exception {
        final JenaIRI ex1 = jena.createIRI("http://example.com/ex1");
        final JenaIRI ex2 = jena.createIRI("http://example.com/ex2");
        final JenaLiteral lit = jena.createLiteral("Hello");

        final JenaGeneralizedTripleLike t = jena.createGeneralizedTriple(ex1, lit, ex2);
        assertEquals(ex1, t.getSubject());
        assertEquals(ex2, t.getObject());
        assertEquals(lit, t.getPredicate()); // it's a literal!
        assertTrue(t.asJenaTriple().getPredicate().isLiteral());
    }


    @Test
    public void literalSubject() throws Exception {
        final JenaIRI ex1 = jena.createIRI("http://example.com/ex1");
        final JenaIRI ex2 = jena.createIRI("http://example.com/ex2");
        final JenaLiteral lit = jena.createLiteral("Hello");

        final JenaGeneralizedTripleLike t = jena.createGeneralizedTriple(lit, ex1, ex2);
        assertEquals(lit, t.getSubject()); // it's a literal!
        assertEquals(ex1, t.getPredicate());
        assertEquals(ex2, t.getObject());
        assertTrue(t.asJenaTriple().getSubject().isLiteral());
    }

    @Test
    public void asGeneralizedTriple() throws Exception {
        final Node s = NodeFactory.createLiteral("Hello");
        final Node p = NodeFactory.createBlankNode();
        final Node o = NodeFactory.createURI("http://example.com/ex");
        final Triple jt = Triple.create(s, p, o);
        final JenaTripleLike t = jena.asGeneralizedTriple(jt);
        assertEquals(jena.createLiteral("Hello"), t.getSubject());
        assertEquals(jena.asRDFTerm(p), t.getPredicate());
        assertEquals(jena.createIRI("http://example.com/ex"), t.getObject());
    }

}
