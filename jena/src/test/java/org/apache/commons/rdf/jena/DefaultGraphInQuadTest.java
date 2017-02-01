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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.simple.SimpleRDF;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.sparql.core.Quad;
import org.junit.Test;

/**
 * COMMONSRDF-55: Ensure correct handling of 
 * Jena's default graph IRI urn:x-arq:DefaultGraph
 */
public class DefaultGraphInQuadTest {
    
    JenaRDF rdf = new JenaRDF();
    IRI example = rdf.createIRI("http://example.com/");
    Node exampleJena = NodeFactory.createURI("http://example.com/");
    
    @Test
    public void createFromNull() throws Exception {        
        JenaQuad q = rdf.createQuad(null, example, example, example);
        assertFalse(q.getGraphName().isPresent());
        assertTrue(q.asJenaQuad().isDefaultGraph());
        assertEquals(Quad.defaultGraphIRI,  q.asJenaQuad().getGraph());
    }

    @Test
    public void createFromDefaultGraphIRI() throws Exception {
        JenaIRI defaultGraph = (JenaIRI) rdf.asRDFTerm(Quad.defaultGraphIRI);        
        JenaQuad q = rdf.createQuad(defaultGraph, example, example, example);
        assertTrue(q.asJenaQuad().isDefaultGraph());
        assertEquals(Quad.defaultGraphIRI,  q.asJenaQuad().getGraph());
        assertFalse(q.getGraphName().isPresent());
    }

    @Test
    public void createFromForeignDefaultGraph() throws Exception {
        // What if <urn:x-arq:DefaultGraph> appear in a non-Jena IRI?
        IRI foreignDefaultGraph = new SimpleRDF().createIRI(Quad.defaultGraphIRI.getURI());
        JenaQuad q = rdf.createQuad(foreignDefaultGraph, example, example, example);
        assertTrue(q.asJenaQuad().isDefaultGraph());
        assertEquals(Quad.defaultGraphIRI,  q.asJenaQuad().getGraph());
        assertFalse(q.getGraphName().isPresent());
    }
    

    @Test
    public void createFromDefaultGraphNodeGeneratedIRINode() throws Exception {
        // What if <urn:x-arq:DefaultGraphNode> appear as an IRI instance?
        IRI foreignDefaultGraph = rdf.createIRI(Quad.defaultGraphNodeGenerated.getURI());
        JenaQuad q = rdf.createQuad(foreignDefaultGraph, example, example, example);
        assertTrue(q.asJenaQuad().isDefaultGraph());
        assertFalse(q.getGraphName().isPresent());
        // Unfortunately  Quad.defaultGraphNodeGenerated is not preserved
        // within JenaQuad
        //assertEquals(Quad.defaultGraphNodeGenerated, q.asJenaQuad().getGraph());
    }

    @Test
    public void createFromDefaultGraphNodeGeneratedIRIString() throws Exception {
        // What if <urn:x-arq:DefaultGraphNode> appear in a non-Jena IRI?
        IRI foreignDefaultGraph = (IRI) rdf.asRDFTerm((Quad.defaultGraphNodeGenerated));
        JenaQuad q = rdf.createQuad(foreignDefaultGraph, example, example, example);
        assertTrue(q.asJenaQuad().isDefaultGraph());
        assertFalse(q.getGraphName().isPresent());
        // Unfortunately  Quad.defaultGraphNodeGenerated is not preserved
        // within JenaQuad
        assertEquals(Quad.defaultGraphNodeGenerated, q.asJenaQuad().getGraph());
    }

    
    @Test
    public void defaultGraphIRI() throws Exception {
        Quad jenaQuad = Quad.create(Quad.defaultGraphIRI, exampleJena, exampleJena, exampleJena);
        JenaQuad q = rdf.asQuad(jenaQuad);        
        assertFalse(q.getGraphName().isPresent());
        assertTrue(q.asJenaQuad().isDefaultGraph());
    }

    @Test
    public void defaultGraphNodeGenerated() throws Exception {        
        // <urn:x-arq:DefaultGraphNode> might appear in parser output
        Quad jenaQuad = Quad.create(Quad.defaultGraphNodeGenerated, exampleJena, exampleJena, exampleJena);
        JenaQuad q = rdf.asQuad(jenaQuad);        
        assertFalse(q.getGraphName().isPresent());
        assertTrue(q.asJenaQuad().isDefaultGraph());
        // Preserves <urn:x-arq:DefaultGraphNode>
        assertEquals(Quad.defaultGraphNodeGenerated, q.asJenaQuad().getGraph());
    }

    @Test
    public void unionGraph() throws Exception {
        // unionGraph shouldn't really appear as a quad except
        // in a pattern
        Quad jenaQuad = Quad.create(Quad.unionGraph, exampleJena, exampleJena, exampleJena);
        JenaQuad q = rdf.asQuad(jenaQuad);
        // But at least we can agree it is NOT (necessarily) in the
        // default graph
        assertFalse(q.asJenaQuad().isDefaultGraph());
        assertTrue(q.getGraphName().isPresent());
    }
}
