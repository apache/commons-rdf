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

package org.apache.jena.commons;

import org.apache.commons.rdf.api.* ;
import org.apache.jena.datatypes.xsd.XSDDatatype ;
import org.apache.jena.graph.Node ;
import org.apache.jena.riot.system.StreamRDF ;

public class ToGraph implements StreamRDF {

    private Graph graph;
    private RDFTermFactory factory;

    public ToGraph(RDFTermFactory factory) {
        this(factory.createGraph(), factory) ;
    }

    public ToGraph(Graph graph, RDFTermFactory factory) {
        this.factory = factory ; 
        this.graph = graph ;
    }

    @Override
    public void start() {}

    @Override
    public void triple(org.apache.jena.graph.Triple triple) {
        graph.add((BlankNodeOrIRI)fromJena(triple.getSubject()), 
                  (IRI)fromJena(triple.getPredicate()),
                  fromJena(triple.getObject())) ;
    }

    // jena to commonsrdf
    public RDFTerm fromJena(Node node) {
        if ( node.isURI() )
            return factory.createIRI(node.getURI()) ;
        if ( node.isLiteral() ) {
            String lang = node.getLiteralLanguage() ;
            if ( lang != null && lang.isEmpty() )
                return factory.createLiteral(node.getLiteralLexicalForm(), lang) ;
            if ( node.getLiteralDatatype().equals(XSDDatatype.XSDstring) )
                return factory.createLiteral(node.getLiteralLexicalForm()) ;
            IRI dt = factory.createIRI(node.getLiteralDatatype().getURI()) ;
            return factory.createLiteral(node.getLiteralLexicalForm(), dt);
        }
        if ( node.isBlank() )
            return factory.createBlankNode(node.getBlankNodeLabel()) ;
        error("Node is not a concrete RDF Term: "+node) ;
        return null ;
    }

    private static void error(String msg) {
        System.err.println("Error: "+msg) ;
        throw new RuntimeException(msg) ;
    }
    
    @Override
    public void quad(org.apache.jena.sparql.core.Quad quad) {
        throw new UnsupportedOperationException() ;
    }

    @Override
    public void base(String base) {}

    @Override
    public void prefix(String prefix, String iri) {}

    @Override
    public void finish() {}



    public Graph getGraph() {
        return graph;
    }
    
}

