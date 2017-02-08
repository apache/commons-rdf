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
import org.junit.Test;

public class GeneralizedRDFQuadTest {

    private JenaRDF jena = new JenaRDF();
    
    @Test
    public void bnodeProperty() throws Exception {
        BlankNode b1 = jena.createBlankNode("b1");
        JenaIRI ex1 = jena.createIRI("http://example.com/ex1");
        JenaIRI ex2 = jena.createIRI("http://example.com/ex2");
        JenaIRI ex3 = jena.createIRI("http://example.com/ex3");
        
        JenaGeneralizedQuadLike t = jena.createGeneralizedQuad(ex1, b1, ex2, ex3);
        assertEquals(ex1, t.getSubject());
        assertEquals(ex2, t.getObject());
        assertEquals(b1, t.getPredicate()); // it's a bnode!
        assertTrue(t.asJenaQuad().getPredicate().isBlank());
    }

    @Test
    public void literalPredicate() throws Exception {
        JenaIRI ex1 = jena.createIRI("http://example.com/ex1");
        JenaIRI ex2 = jena.createIRI("http://example.com/ex2");
        JenaIRI ex3 = jena.createIRI("http://example.com/ex3");
        JenaLiteral lit = jena.createLiteral("Hello");
        
        JenaGeneralizedQuadLike t = jena.createGeneralizedQuad(ex1, lit, ex2, ex3);
        assertEquals(ex1, t.getSubject());
        assertEquals(ex2, t.getObject());
        assertEquals(lit, t.getPredicate()); // it's a literal!
        assertTrue(t.asJenaQuad().getPredicate().isLiteral());
    }


    @Test
    public void literalSubject() throws Exception {
        JenaIRI ex1 = jena.createIRI("http://example.com/ex1");
        JenaIRI ex2 = jena.createIRI("http://example.com/ex2");
        JenaIRI ex3 = jena.createIRI("http://example.com/ex3");
        JenaLiteral lit = jena.createLiteral("Hello");
        
        JenaGeneralizedQuadLike t = jena.createGeneralizedQuad(lit, ex1, ex2, ex3);
        assertEquals(lit, t.getSubject()); // it's a literal!
        assertEquals(ex1, t.getPredicate());
        assertEquals(ex2, t.getObject());
        assertTrue(t.asJenaQuad().getSubject().isLiteral());
    }
    

    @Test
    public void literalGraph() throws Exception {
        JenaIRI ex1 = jena.createIRI("http://example.com/ex1");
        JenaIRI ex2 = jena.createIRI("http://example.com/ex2");
        JenaIRI ex3 = jena.createIRI("http://example.com/ex3");
        JenaLiteral lit = jena.createLiteral("Hello");
        
        JenaGeneralizedQuadLike t = jena.createGeneralizedQuad(ex1, ex2, ex3, lit);
        assertEquals(ex1, t.getSubject()); 
        assertEquals(ex2, t.getPredicate());
        assertEquals(ex3, t.getObject());
        assertTrue(t.getGraphName().isPresent());
        assertEquals(lit, t.getGraphName().get());
        assertTrue(t.asJenaQuad().getGraph().isLiteral());
    }
    
    
}
