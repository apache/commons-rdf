/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
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
package org.apache.commons.rdf.simple;

import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.rdf.api.BlankNode;
import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Quad;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.Triple;

/**
 * A {@link Graph} view on a {@link Dataset}.
 * <p>
 * This view is backed by a {@link Dataset}, and can be constructed in two ways:
 *
 * <dl>
 * <dt>{@link #DatasetGraphView(Dataset)}</dt>
 * <dd>Expose a <em>union graph</em> view of the Dataset, where all the
 * {@link Quad}s of the Dataset is represented as a {@link Triple}. Adding
 * triples will add them to the <em>default graph</em>, while removing triples
 * will remove from all graphs.</dd>
 *
 * <dt>{@link #DatasetGraphView(Dataset, BlankNodeOrIRI)}</dt>
 * <dd>Expose a particular graph of the Dataset, either named by an {@link IRI},
 * a {@link BlankNode}, or <code>null</code> for the <em>default
 * graph</em>.</dd>
 * </dl>
 * <p>
 * Changes in the Graph are reflected directly in the Dataset and vice versa.
 * This class is thread-safe is the underlying Dataset is thread-safe.
 */
public class DatasetGraphView implements Graph {

    private final boolean unionGraph;
    private final BlankNodeOrIRI namedGraph;
    private final Dataset dataset;

    public DatasetGraphView(final Dataset dataset) {
        this.dataset = dataset;
        this.namedGraph = null;
        this.unionGraph = true;
    }

    public DatasetGraphView(final Dataset dataset, final BlankNodeOrIRI namedGraph) {
        this.dataset = dataset;
        this.namedGraph = namedGraph;
        this.unionGraph = false;
    }

    @Override
    public void close() throws Exception {
        dataset.close();

    }

    @Override
    public void add(final Triple triple) {
        dataset.add(namedGraph, triple.getSubject(), triple.getPredicate(), triple.getObject());
    }

    @Override
    public void add(final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object) {
        dataset.add(namedGraph, subject, predicate, object);
    }

    @Override
    public boolean contains(final Triple triple) {
        return dataset.contains(unionOrNamedGraph(), triple.getSubject(), triple.getPredicate(), triple.getObject());
    }

    private Optional<BlankNodeOrIRI> unionOrNamedGraph() {
        if (unionGraph) {
            return null;
        }
        return Optional.ofNullable(namedGraph);
    }

    @Override
    public boolean contains(final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object) {
        return dataset.contains(unionOrNamedGraph(), subject, predicate, object);
    }

    @Override
    public void remove(final Triple triple) {
        dataset.remove(unionOrNamedGraph(), triple.getSubject(), triple.getPredicate(), triple.getObject());
    }

    @Override
    public void remove(final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object) {
        dataset.remove(unionOrNamedGraph(), subject, predicate, object);
    }

    @Override
    public void clear() {
        dataset.remove(unionOrNamedGraph(), null, null, null);
    }

    @Override
    public long size() {
        return stream().count();
    }

    @Override
    public Stream<? extends Triple> stream() {
        return stream(null, null, null);
    }

    @Override
    public Stream<? extends Triple> stream(final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object) {
        final Stream<Triple> stream = dataset.stream(unionOrNamedGraph(), subject, predicate, object).map(Quad::asTriple);
        if (unionGraph) {
            // remove duplicates
            return stream.distinct();
        }
        return stream;
    }

}
