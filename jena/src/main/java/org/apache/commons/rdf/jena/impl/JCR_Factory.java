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

package org.apache.commons.rdf.jena.impl;
import static org.apache.commons.rdf.jena.JenaCommonsRDF.conversionError;

import org.apache.commons.rdf.api.* ;
import org.apache.commons.rdf.jena.JenaCommonsRDF;
import org.apache.jena.graph.Node ;
import org.apache.jena.graph.NodeFactory ;
import org.apache.jena.sparql.graph.GraphFactory ;

public class JCR_Factory {
    // basic components to commonsrdf backed by Jena. 
    public static IRI createIRI(String iriStr) {
        return (IRI)JenaCommonsRDF.fromJena(NodeFactory.createURI(iriStr));
    }

    public static Literal createLiteral(String lexStr) {
        return new JCR_Literal(NodeFactory.createLiteral(lexStr));
    }

    public static Literal createLiteralDT(String lexStr, String datatypeIRI) {
        return new JCR_Literal(NodeFactory.createLiteral(lexStr, NodeFactory.getType(datatypeIRI))) ;
    }

    public static Literal createLiteralLang(String lexStr, String langTag) {
        return new JCR_Literal(NodeFactory.createLiteral(lexStr, langTag));
    }

    public static BlankNode createBlankNode() {
        return new JCR_BlankNode(NodeFactory.createBlankNode());
    }

    public static BlankNode createBlankNode(String id) {
        return new JCR_BlankNode(NodeFactory.createBlankNode(id));
    }
    
    public static Graph createGraph() { return new JCR_Graph(GraphFactory.createDefaultGraph()) ; }
    
    public static Triple createTriple(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) { 
        return new JCR_Triple(subject, predicate, object) ;
    }
    
    public static Triple fromJena(org.apache.jena.graph.Triple triple) {
        return new JCR_Triple(triple) ;
    }

    public static Graph fromJena(org.apache.jena.graph.Graph graph) {
        return new JCR_Graph(graph) ;
    }

    public static RDFTerm fromJena(Node node) {
        if ( node.isURI() )
            return new JCR_IRI(node) ; 
        if ( node.isLiteral() ) {
            return new JCR_Literal(node) ; 
//            String lang = node.getLiteralLanguage() ;
//            if ( lang != null && lang.isEmpty() )
//                return createLiteralLang(node.getLiteralLexicalForm(), lang) ;
//            if ( node.getLiteralDatatype().equals(XSDDatatype.XSDstring) )
//                return createLiteral(node.getLiteralLexicalForm()) ;
//            return createLiteralDT(node.getLiteralLexicalForm(), node.getLiteralDatatype().getURI());
        }
        if ( node.isBlank() )
            return new JCR_BlankNode(node) ; 
        conversionError("Node is not a concrete RDF Term: "+node) ;
        return null ;
    }
}

