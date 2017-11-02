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

import static org.apache.jena.graph.Node.ANY;

import java.io.StringWriter;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Quad;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.jena.JenaDataset;
import org.apache.commons.rdf.jena.JenaGraph;
import org.apache.commons.rdf.jena.JenaRDF;
import org.apache.jena.atlas.iterator.Iter;
import org.apache.jena.graph.Node;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.GraphView;

class JenaDatasetImpl implements JenaDataset {

    private final DatasetGraph datasetGraph;
    private final UUID salt;
    private final JenaRDF factory;

    JenaDatasetImpl(final DatasetGraph datasetGraph, final UUID salt) {
        this.datasetGraph = datasetGraph;
        this.salt = salt;
        this.factory = new JenaRDF(salt);
    }

    @Override
    public void add(final BlankNodeOrIRI graphName, final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object) {
        datasetGraph.add(org.apache.jena.sparql.core.Quad.create(factory.asJenaNode(graphName), factory.asJenaNode(subject),
                factory.asJenaNode(predicate), factory.asJenaNode(object)));
    }

    @Override
    public void add(final Quad quad) {
        datasetGraph.add(factory.asJenaQuad(quad));
    }

    @Override
    public DatasetGraph asJenaDatasetGraph() {
        return datasetGraph;
    }

    @Override
    public void clear() {
        datasetGraph.clear();
    }

    @Override
    public void close() {
        datasetGraph.close();
    }

    @Override
    public boolean contains(final Optional<BlankNodeOrIRI> graphName, final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object) {
        return datasetGraph.contains(toJenaPattern(graphName), toJenaPattern(subject), toJenaPattern(predicate),
                toJenaPattern(object));
    }

    private Node toJenaPattern(final Optional<? extends RDFTerm> graphName) {
        // In theory we could have done:
        // factory.toJena(graphName.orElse(internalJenaFactory::createAnyVariable))
        // but because of generics casting rules that doesn't work :(

        if (graphName == null) {
            return ANY;
        }
        // null: default datasetGraph
        return factory.asJenaNode(graphName.orElse(null));
    }

    private Node toJenaPattern(final RDFTerm term) {
        if (term == null) {
            return ANY;
        }
        return factory.asJenaNode(term);
    }

    @Override
    public boolean contains(final Quad quad) {
        return datasetGraph.contains(factory.asJenaQuad(quad));
    }

    @Override
    public void remove(final Optional<BlankNodeOrIRI> graphName, final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object) {
        datasetGraph.deleteAny(toJenaPattern(graphName), toJenaPattern(subject),
                toJenaPattern(predicate), toJenaPattern(object));
    }

    @Override
    public void remove(final Quad quad) {
        // COMMONSRDF-51:
        datasetGraph.deleteAny(
                toJenaPattern(quad.getGraphName()),
                toJenaPattern(quad.getSubject()),
                toJenaPattern(quad.getPredicate()),
                toJenaPattern(quad.getObject()));
    }

    @Override
    public long size() {
        final long quads = Iter.asStream(datasetGraph.listGraphNodes())
                .map(datasetGraph::getGraph)
                .collect(Collectors.summingLong(org.apache.jena.graph.Graph::size));
        return quads + datasetGraph.getDefaultGraph().size();
    }

    @Override
    public Stream<? extends Quad> stream() {
        final JenaRDF factory = new JenaRDF(salt);
        return Iter.asStream(datasetGraph.find(ANY, ANY, ANY, ANY), true).map(factory::asQuad);
    }

    @Override
    public Stream<? extends Quad> stream(final Optional<BlankNodeOrIRI> g, final BlankNodeOrIRI s, final IRI p, final RDFTerm o) {
        final JenaRDF factory = new JenaRDF(salt);
        return Iter.asStream(datasetGraph.find(toJenaPattern(g), toJenaPattern(s), toJenaPattern(p), toJenaPattern(o)), true)
                .map(factory::asQuad);
    }

    @Override
    public String toString() {
        final StringWriter sw = new StringWriter();
        RDFDataMgr.write(sw, datasetGraph, Lang.NQUADS);
        return sw.toString();
    }

    @Override
    public Graph getGraph() {
        final GraphView g = GraphView.createDefaultGraph(datasetGraph);
        return new JenaGraphImpl(g, salt);
    }

    @Override
    public JenaGraph getUnionGraph() {
        final GraphView gv = GraphView.createUnionGraph(datasetGraph);
        return new JenaGraphImpl(gv, salt);
    }

    @Override
    public Optional<Graph> getGraph(final BlankNodeOrIRI graphName) {
        final GraphView gv = GraphView.createNamedGraph(datasetGraph, factory.asJenaNode(graphName));
        return Optional.of(new JenaGraphImpl(gv, salt));
    }

    @Override
    public Stream<BlankNodeOrIRI> getGraphNames() {
        final JenaRDF factory = new JenaRDF(salt);
        return Iter.asStream(datasetGraph.listGraphNodes()).map(node -> (BlankNodeOrIRI) factory.asRDFTerm(node));
    }

    @Override
    public Iterable<Quad> iterate() {
        final JenaRDF factory = new JenaRDF(salt);
        return Iter.asStream(datasetGraph.find(), false).map(q -> (Quad) factory.asQuad(q))::iterator;
    }

}
