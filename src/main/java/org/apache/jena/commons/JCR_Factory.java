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
import org.apache.jena.datatypes.RDFDatatype ;
import org.apache.jena.graph.Node ;
import org.apache.jena.graph.NodeFactory ;
import org.apache.jena.sparql.graph.GraphFactory ;

public class JCR_Factory {
    // basic components to commonsrdf backed by Jena. 
    public static IRI createIRI(String iriStr) {
        return (IRI)fromJena(NodeFactory.createURI(iriStr));
    }

    public static Literal createLiteral(String lexStr) {
        return (Literal)fromJena(NodeFactory.createLiteral(lexStr));
    }

    public static Literal createLiteralDT(String lexStr, String datatypeIRI) {
        return (Literal)fromJena(NodeFactory.createLiteral(lexStr, NodeFactory.getType(datatypeIRI))) ;
    }

    public static Literal createLiteralLang(String lexStr, String langTag) {
        return (Literal)fromJena(NodeFactory.createLiteral(lexStr, langTag));
    }

    public static BlankNode createBlankNode() {
        return (BlankNode)fromJena(NodeFactory.createBlankNode());
    }

    public static BlankNode createBlankNode(String id) {
        return (BlankNode)fromJena(NodeFactory.createBlankNode(id));
    }
    
    public static Graph createGraph() { return new JCR_Graph(GraphFactory.createDefaultGraph()) ; }
    
    public static Triple createTriple(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) { 
        return new JCR_Triple(subject, predicate, object) ;
    }
    
//    // Nodes to commonsrdf. 
//    public static IRI createIRI(Node iri) { return ()
//    public static Literal createLiteral(Node lexStr) { return null ; }
//    public static BlankNode createBlankNode(Node bNode) { return null ; }

    
    // commonsrdf to jena
    public static org.apache.jena.graph.Triple toJena(Triple triple) {
        return new org.apache.jena.graph.Triple(toJena(triple.getSubject()), toJena(triple.getPredicate()), toJena(triple.getObject()) ) ;   
    }
    
    public static Node toJena(RDFTerm term) {
        if ( term instanceof JenaCommonsRDF )
            return ((JenaCommonsRDF)term).getNode() ;
        
        if ( term instanceof IRI ) 
            return NodeFactory.createURI(((IRI)term).getIRIString()) ;
        
        if ( term instanceof Literal ) {
            Literal lit = (Literal)term ; 
            RDFDatatype dt = NodeFactory.getType(lit.getDatatype().getIRIString()) ;
            String lang = lit.getLanguageTag().orElse("") ;
            return NodeFactory.createLiteral(lit.getLexicalForm(), lang, dt) ; 
        }
        
        if ( term instanceof BlankNode ) {
            String id = ((BlankNode)term).uniqueReference() ;
            // XXX ???
            // preserve skolemization vs gen new id?
            return NodeFactory.createBlankNode(id) ;
        }
        
        error("Not a concrete RDF Term: "+term) ;
        return null ;
    }
    // jena to commonsrdf
    public static RDFTerm fromJena(Node node) {
        if ( node.isURI() )
            return new JCR_IRI(node) ; 
        if ( node.isLiteral() )
            return new JCR_Literal(node) ;
        if ( node.isBlank() ) {
            return new JCR_BlankNode(node) ;
        }
        error("Node is not a concrete RDF Term: "+node) ;
        return null ;
    }
 
    public static Triple fromJena(org.apache.jena.graph.Triple triple) {
        return createTriple((BlankNodeOrIRI)fromJena(triple.getSubject()),
                            (IRI)fromJena(triple.getPredicate()),
                            fromJena(triple.getObject())) ;
    }
    private static void error(String msg) {
        System.err.println("Error: "+msg) ;
        throw new RuntimeException(msg) ;
    }
}

