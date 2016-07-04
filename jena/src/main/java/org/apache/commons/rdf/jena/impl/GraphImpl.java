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

import java.io.StringWriter ;
import java.util.stream.Stream ;

import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.Triple;
import org.apache.commons.rdf.jena.JenaCommonsRDF;
import org.apache.commons.rdf.jena.JenaGraph;
import org.apache.jena.atlas.iterator.Iter ;
import org.apache.jena.graph.Node ;
import org.apache.jena.riot.Lang ;
import org.apache.jena.riot.RDFDataMgr ;

public class GraphImpl implements Graph, JenaGraph {

    private org.apache.jena.graph.Graph graph;

    /*package*/ GraphImpl(org.apache.jena.graph.Graph graph) {
        this.graph = graph ;
    }
    
    @Override
    public org.apache.jena.graph.Graph getGraph() {
        return graph ;
    }

    @Override
    public void add(Triple triple) { graph.add(JenaCommonsRDF.toJena(triple)); }

    @Override
    public void add(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) { 
        graph.add(org.apache.jena.graph.Triple.create(JenaCommonsRDF.toJena(subject),
                                                      JenaCommonsRDF.toJena(predicate),
                                                      JenaCommonsRDF.toJena(object)));
    }

    @Override
    public boolean contains(Triple triple) {
        return graph.contains(JenaCommonsRDF.toJena(triple)) ; 
    }

    @Override
    public boolean contains(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
        return graph.contains(JenaCommonsRDF.toJena(subject),
                              JenaCommonsRDF.toJena(predicate),
                              JenaCommonsRDF.toJena(object) );
    }

    @Override
    public void remove(Triple triple) { graph.delete(JenaCommonsRDF.toJena(triple)); }

    @Override
    public void remove(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
        graph.delete(org.apache.jena.graph.Triple.create(JenaCommonsRDF.toJena(subject),
                                                         JenaCommonsRDF.toJena(predicate),
                                                         JenaCommonsRDF.toJena(object)));
    }

    @Override
    public void clear() { graph.clear(); }

    @Override
    public long size() {
        return graph.size() ;
    }

    @Override
    public Stream<? extends Triple> stream() {
    	return Iter.asStream(graph.find(null, null, null), true).map(JenaCommonsRDF::fromJena);
    }

    @Override
    public Stream<? extends Triple> stream(BlankNodeOrIRI s, IRI p, RDFTerm o) {
        return Iter.asStream(graph.find(toJenaAny(s),toJenaAny(p),toJenaAny(o)), true).
        		map(JenaCommonsRDF::fromJena);
    }

    private Node toJenaAny(RDFTerm term) {
        if ( term == null )
            return Node.ANY ;
        return JenaCommonsRDF.toJena(term) ;
    }

    @Override
    public void close() { graph.close(); }
    
    @Override
    public String toString() {
        StringWriter sw = new StringWriter() ;
        RDFDataMgr.write(sw, graph, Lang.NT) ;
        return sw.toString() ;
    }

}

