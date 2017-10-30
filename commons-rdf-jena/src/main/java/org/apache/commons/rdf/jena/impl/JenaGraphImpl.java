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

import java.io.StringWriter;
import java.util.UUID;
import java.util.stream.Stream;

import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Literal;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.Triple;
import org.apache.commons.rdf.jena.JenaGraph;
import org.apache.commons.rdf.jena.JenaRDF;
import org.apache.jena.atlas.iterator.Iter;
import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

class JenaGraphImpl implements JenaGraph {

    private final org.apache.jena.graph.Graph graph;
    private final UUID salt;
    private final transient JenaRDF factory;
    private Model model;

    JenaGraphImpl(final org.apache.jena.graph.Graph graph, final UUID salt) {
        this.graph = graph;
        this.salt = salt;
        this.factory = new JenaRDF(salt);
    }

    JenaGraphImpl(final Model model, final UUID salt) {
        this.model = model;
        this.graph = model.getGraph();
        this.salt = salt;
        this.factory = new JenaRDF(salt);
    }

    @Override
    public void add(final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object) {
        graph.add(org.apache.jena.graph.Triple.create(factory.asJenaNode(subject), factory.asJenaNode(predicate),
                factory.asJenaNode(object)));
    }

    @Override
    public void add(final Triple triple) {
        graph.add(factory.asJenaTriple(triple));
    }

    @Override
    public org.apache.jena.graph.Graph asJenaGraph() {
        return graph;
    }

    @Override
    public void clear() {
        graph.clear();
    }

    @Override
    public void close() {
        graph.close();
    }

    @Override
    public boolean contains(final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object) {
        return graph.contains(toJenaPattern(subject), toJenaPattern(predicate), toJenaPattern(object));
    }

    @Override
    public boolean contains(final Triple triple) {
        return graph.contains(factory.asJenaTriple(triple));
    }

    @Override
    public void remove(final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object) {
        graph.remove(toJenaPattern(subject), toJenaPattern(predicate),
                toJenaPattern(object));
    }

    private Node toJenaPattern(final RDFTerm pattern) {
        if (pattern == null) {
            return Node.ANY;
        }
        return factory.asJenaNode(pattern);
    }

    @Override
    public void remove(final Triple triple) {
        if ((triple.getObject() instanceof Literal) &&
                ((Literal) triple.getObject()).getLanguageTag().isPresent()) {
            // COMMONSRDF-51: graph.delete(Triple) would be too restrictive
            // as it won't delete triples with different lang tag - so
            // we'll need to use the pattern matching graph.remove instead()
            graph.remove(
                    factory.asJenaNode(triple.getSubject()),
                    factory.asJenaNode(triple.getPredicate()),
                    factory.asJenaNode(triple.getObject()));
        } else {
            graph.delete(factory.asJenaTriple(triple));
        }
    }

    @Override
    public long size() {
        return graph.size();
    }

    @Override
    public Stream<? extends Triple> stream() {
        final JenaRDF factory = new JenaRDF(salt);
        return Iter.asStream(graph.find(null, null, null), true).map(factory::asTriple);
    }

    @Override
    public Stream<? extends Triple> stream(final BlankNodeOrIRI s, final IRI p, final RDFTerm o) {
        final JenaRDF factory = new JenaRDF(salt);
        return Iter.asStream(graph.find(toJenaAny(s), toJenaAny(p), toJenaAny(o)), true).map(factory::asTriple);
    }

    private Node toJenaAny(final RDFTerm term) {
        if (term == null) {
            return Node.ANY;
        }
        return factory.asJenaNode(term);
    }

    @Override
    public String toString() {
        final StringWriter sw = new StringWriter();
        RDFDataMgr.write(sw, graph, Lang.NT);
        return sw.toString();
    }

    @Override
    public Model asJenaModel() {
        if (model == null) {
            synchronized (this) {
                // As Model can be used for locks, we should make sure we don't
                // make
                // more than one model
                if (model == null) {
                    model = ModelFactory.createModelForGraph(graph);
                }
            }
        }
        return model;
    }

}
