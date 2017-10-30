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

import static org.junit.Assert.*;

import org.apache.commons.rdf.api.BlankNode;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.sparql.core.Quad;
import org.junit.Test;

public class GeneralizedRDFQuadTest {

    private final JenaRDF jena = new JenaRDF();

    @Test
    public void bnodeProperty() throws Exception {
        final BlankNode b1 = jena.createBlankNode("b1");
        final JenaIRI ex1 = jena.createIRI("http://example.com/ex1");
        final JenaIRI ex2 = jena.createIRI("http://example.com/ex2");
        final JenaIRI ex3 = jena.createIRI("http://example.com/ex3");

        final JenaGeneralizedQuadLike q = jena.createGeneralizedQuad(ex1, b1, ex2, ex3);
        assertEquals(ex1, q.getSubject());
        assertEquals(ex2, q.getObject());
        assertEquals(b1, q.getPredicate()); // it's a bnode!
        assertEquals(ex3, q.getGraphName().get());
        assertTrue(q.asJenaQuad().getPredicate().isBlank());
    }

    @Test
    public void literalPredicate() throws Exception {
        final JenaIRI ex1 = jena.createIRI("http://example.com/ex1");
        final JenaIRI ex2 = jena.createIRI("http://example.com/ex2");
        final JenaIRI ex3 = jena.createIRI("http://example.com/ex3");
        final JenaLiteral lit = jena.createLiteral("Hello");

        final JenaGeneralizedQuadLike q = jena.createGeneralizedQuad(ex1, lit, ex2, ex3);
        assertEquals(ex1, q.getSubject());
        assertEquals(ex2, q.getObject());
        assertEquals(lit, q.getPredicate()); // it's a literal!
        assertEquals(ex3, q.getGraphName().get());
        assertTrue(q.asJenaQuad().getPredicate().isLiteral());
    }


    @Test
    public void literalSubject() throws Exception {
        final JenaIRI ex1 = jena.createIRI("http://example.com/ex1");
        final JenaIRI ex2 = jena.createIRI("http://example.com/ex2");
        final JenaIRI ex3 = jena.createIRI("http://example.com/ex3");
        final JenaLiteral lit = jena.createLiteral("Hello");

        final JenaGeneralizedQuadLike q = jena.createGeneralizedQuad(lit, ex1, ex2, ex3);
        assertEquals(lit, q.getSubject()); // it's a literal!
        assertEquals(ex1, q.getPredicate());
        assertEquals(ex2, q.getObject());
        assertEquals(ex3, q.getGraphName().get());
        assertTrue(q.asJenaQuad().getSubject().isLiteral());
    }

    @Test
    public void literalSubjectDefaultGraphGen() throws Exception {
        final JenaIRI ex1 = jena.createIRI("http://example.com/ex1");
        final JenaIRI ex2 = jena.createIRI("http://example.com/ex2");
        // No need to cast to JenaIRI
        final JenaRDFTerm defG = jena.asRDFTerm(Quad.defaultGraphNodeGenerated);
        final JenaLiteral lit = jena.createLiteral("Hello");

        final JenaGeneralizedQuadLike q = jena.createGeneralizedQuad(lit, ex1, ex2, defG);
        assertEquals(lit, q.getSubject()); // it's a literal!
        assertEquals(ex1, q.getPredicate());
        assertEquals(ex2, q.getObject());
        assertTrue(q.asJenaQuad().getSubject().isLiteral());
        assertFalse(q.getGraphName().isPresent());
        assertTrue(q.asJenaQuad().isDefaultGraph());
    }

    @Test
    public void asGeneralizedQuad() throws Exception {
        final Node s = NodeFactory.createLiteral("Hello");
        final Node p = NodeFactory.createBlankNode();
        final Node o = NodeFactory.createURI("http://example.com/ex");
        final Node g = Quad.defaultGraphIRI;
        final Quad jq = Quad.create(g, s, p, o);
        final JenaQuadLike<RDFTerm> q = jena.asGeneralizedQuad(jq);
        assertEquals(jena.createLiteral("Hello"), q.getSubject());
        assertEquals(jena.asRDFTerm(p), q.getPredicate());
        assertEquals(jena.createIRI("http://example.com/ex"), q.getObject());
        assertFalse(q.getGraphName().isPresent());
    }

    @Test
    public void literalGraph() throws Exception {
        final JenaIRI ex1 = jena.createIRI("http://example.com/ex1");
        final JenaIRI ex2 = jena.createIRI("http://example.com/ex2");
        final JenaIRI ex3 = jena.createIRI("http://example.com/ex3");
        final JenaLiteral lit = jena.createLiteral("Hello");

        final JenaGeneralizedQuadLike q = jena.createGeneralizedQuad(ex1, ex2, ex3, lit);
        assertEquals(ex1, q.getSubject());
        assertEquals(ex2, q.getPredicate());
        assertEquals(ex3, q.getObject());
        assertEquals(lit, q.getGraphName().get()); // it's a literal!
        assertTrue(q.asJenaQuad().getGraph().isLiteral());
    }



}
