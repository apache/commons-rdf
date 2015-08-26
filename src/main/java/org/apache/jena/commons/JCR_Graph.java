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

import static org.apache.jena.commons.JCR_Factory.toJena ;
import static org.apache.jena.commons.JCR_Factory.fromJena ;

import java.io.StringWriter ;
import java.util.Iterator ;
import java.util.stream.Stream ;

import org.apache.commons.rdf.api.* ;
import org.apache.jena.atlas.iterator.Iter ;
import org.apache.jena.graph.Node ;
import org.apache.jena.riot.Lang ;
import org.apache.jena.riot.RDFDataMgr ;

public class JCR_Graph implements Graph {

    private org.apache.jena.graph.Graph graph;

    /*package*/ JCR_Graph(org.apache.jena.graph.Graph graph) {
        this.graph = graph ;
    }
    
    @Override
    public void add(Triple triple) { graph.add(JCR_Factory.toJena(triple)); }

    @Override
    public void add(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) { 
        graph.add(org.apache.jena.graph.Triple.create(toJena(subject),
                                                      toJena(predicate),
                                                      toJena(object)));
    }

    @Override
    public boolean contains(Triple triple) {
        return graph.contains(toJena(triple)) ; 
    }

    @Override
    public boolean contains(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
        return graph.contains(toJena(subject),
                              toJena(predicate),
                              toJena(object) );
    }

    @Override
    public void remove(Triple triple) { graph.delete(JCR_Factory.toJena(triple)); }

    @Override
    public void remove(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
        graph.delete(org.apache.jena.graph.Triple.create(toJena(subject),
                                                         toJena(predicate),
                                                         toJena(object)));
    }

    @Override
    public void clear() { graph.clear(); }

    @Override
    public long size() {
        return graph.size() ;
    }

    @Override
    public Stream<? extends Triple> getTriples() {
        return getTriples(null, null, null) ;
    }

    @Override
    public Stream<? extends Triple> getTriples(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
        Iterator<org.apache.jena.graph.Triple> iter = graph.find(toJenaAny(subject),toJenaAny(predicate),toJenaAny(object)) ;
        Iterator<Triple> iter2 = Iter.map(iter, t-> fromJena(t)) ;
        return Iter.asStream(iter2) ;
                        
    }

    private Node toJenaAny(RDFTerm term) {
        if ( term == null )
            return Node.ANY ;
        return toJena(term) ;
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

