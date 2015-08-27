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

package org.apache.jena.commonsrdf;

import org.apache.commons.rdf.api.* ;
import org.apache.jena.atlas.lib.NotImplemented ;
import org.apache.jena.commonsrdf.impl.* ;
import org.apache.jena.datatypes.RDFDatatype ;
import org.apache.jena.datatypes.xsd.XSDDatatype ;
import org.apache.jena.graph.Node ;
import org.apache.jena.graph.NodeFactory ;
import org.apache.jena.riot.system.StreamRDF ;
import org.apache.jena.sparql.graph.GraphFactory ;

/** A set of utilities for moving between CommonsRDF and Jena
 * The {@code RDFTermFactory} is {@link RDFTermFactoryJena} for
 * creating CommonsRDF objects backed by Jena.   

 * This class encapsulates moving between existing object (RDFTerms, Triples, Graphs)
 * if necessary.   
 * 
 *  @see RDFTermFactoryJena
 */
public class JenaCommonsRDF {

    /** Convert a CommonsRDF RDFTerm to a Jena Node.
     * If the RDFTerm was from Jena originally, return that original object else
     * create a copy using Jena objects. 
     */
    public static Node toJena(RDFTerm term) {
        if ( term instanceof JenaNode )
            return ((JenaNode)term).getNode() ;
        
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
            return NodeFactory.createBlankNode(id) ;
        }
        conversionError("Not a concrete RDF Term: "+term) ;
        return null ;
    }

    /** Convert a CommonsRDF Triple to a Jena Triple.
     * If the Triple was from Jena originally, return that original object else
     * create a copy using Jena objects. 
     */
    public static org.apache.jena.graph.Triple toJena(Triple triple) {
        if ( triple instanceof JenaTriple )
            return ((JenaTriple)triple).getTriple() ;
        return new org.apache.jena.graph.Triple(toJena(triple.getSubject()), toJena(triple.getPredicate()), toJena(triple.getObject()) ) ;   
    }

    /** Convert a CommonsRDF Graph to a Jena Graph.
     * If the Graph was from Jena originally, return that original object else
     * create a copy using Jena objects. 
     */
    public static org.apache.jena.graph.Graph toJena(Graph graph) {
        if ( graph instanceof JenaGraph )
            return ((JenaGraph)graph).getGraph() ;
        org.apache.jena.graph.Graph g = GraphFactory.createGraphMem() ;
        graph.getTriples().forEach(t->g.add(toJena(t))) ; 
        return g ;   
    }

    /** Adapt an existing Jena Node to CommonsRDF. */
    public static RDFTerm fromJena( Node node) {
        return JCR_Factory.fromJena(node) ;
    }

    /** Adapt an existing Jena Triple to CommonsRDF. */
    public static Triple fromJena(org.apache.jena.graph.Triple triple) {
        return JCR_Factory.fromJena(triple) ;
    }

    /** Adapt an existring Jena Graph to CommonsRDF.  This does not take a copy. */  
    public static Graph fromJena(org.apache.jena.graph.Graph graph) {
        return JCR_Factory.fromJena(graph) ;
    }

    /** Convert from Jena to any RDFCommons implementation */
    public static RDFTerm fromJena(RDFTermFactory factory, Node node) {
        if ( node.isURI() )
            return factory.createIRI(node.getURI()) ;
        if ( node.isLiteral() ) {
            String lang = node.getLiteralLanguage() ;
            if ( lang != null && ! lang.isEmpty() )
                return factory.createLiteral(node.getLiteralLexicalForm(), lang) ;
            if ( node.getLiteralDatatype().equals(XSDDatatype.XSDstring) )
                return factory.createLiteral(node.getLiteralLexicalForm()) ;
            IRI dt = factory.createIRI(node.getLiteralDatatype().getURI()) ;
            return factory.createLiteral(node.getLiteralLexicalForm(), dt);
        }
        if ( node.isBlank() )
            return factory.createBlankNode(node.getBlankNodeLabel()) ;
        //error("Node is not a concrete RDF Term: "+node) ;
        return null ;
    }

    /** Convert from Jena to any RDFCommons implementation */
   public static Triple fromJena(RDFTermFactory factory, org.apache.jena.graph.Triple triple) {
        BlankNodeOrIRI subject = (BlankNodeOrIRI)(fromJena(factory, triple.getSubject())) ;
        IRI predicate = (IRI)(fromJena(factory, triple.getPredicate())) ;
        RDFTerm object = fromJena(factory, triple.getObject()) ;
        return factory.createTriple(subject, predicate, object) ;
    }

   /** Convert from Jena to any RDFCommons implementation */
   public static Graph fromJena(RDFTermFactory factory, org.apache.jena.graph.Graph graph) {
       if ( factory instanceof RDFTermFactoryJena ) 
           return JCR_Factory.fromJena(graph) ;
       throw new NotImplemented() ;
    //        /return JCR_Factory.fromJena(factory, graph) ;
   }

   /** Convert from Jena to any RDFCommons implementation */
   public static StreamRDF streamJenaToCommonsRDF(RDFTermFactory factory, Graph graph) {
       return new ToGraph(factory, graph) ;
   }

   /** Convert from Jena to any RDFCommons implementation */
   public static StreamRDF steramJenaToCommonsRDF(Graph graph) {
       return streamJenaToCommonsRDF(new RDFTermFactoryJena(), graph) ;
   }

   public static void conversionError(String msg) {
        throw new ConversionException(msg) ;
    }
}

