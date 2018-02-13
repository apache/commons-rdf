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
package org.apache.commons.rdf.rdf4j;

import java.util.ConcurrentModificationException;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.Triple;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.repository.Repository;

/**
 * Marker interface for RDF4J implementations of Graph.
 *
 * @see RDF4J#createGraph()
 * @see RDF4J#asGraph(Model)
 * @see RDF4J#asGraph(Repository, Option...)
 * @see RDF4J#asGraphUnion(Repository, Option...)
 * @see RDF4JDataset#getGraph()
 * @see RDF4JDataset#getGraph(BlankNodeOrIRI)
 */
public interface RDF4JGraph extends Graph, RDF4JGraphLike<Triple> {

    /**
     * Return a copy of the context mask as a {@link Set} of
     * {@link RDF4JBlankNodeOrIRI} graph names.
     * <p>
     * If the set is not {@link Set#isEmpty()}, the mask determines which
     * <em>contexts</em> in the corresponding RDF4J {@link Model} or
     * {@link Repository} that this graph reflect. Modifications to the graph
     * (e.g. {@link #add(Triple)} will be performed for all the specified
     * contexts, while retrieval (e.g. {@link #contains(Triple)}) will succeed
     * if the triple is in at least one of the specified contexts.
     * <p>
     * The context mask array may contain <code>null</code>, indicating the
     * default context (the <em>default graph</em> in RDF datasets).
     * <p>
     * If the context mask is {@link Set#isEmpty()}, then this is a <em>union
     * graph</em> which triples reflect statements in any contexts. Triples
     * added to the graph will be added in the default context, e.g. equivalent
     * to <code>new Resource[1]{null}</code>) in RDF4J.
     * <p>
     * Note that the context mask itself cannot be <code>null</code>.
     * <p>
     * The returned set is an immutable copy; to specify a different mask, use
     * {@link RDF4J#asGraph(Repository, Set, Option...)}
     *
     * @return The context mask as a set of {@link BlankNodeOrIRI} graph names,
     *         which may contain the value <code>null</code>.
     */
    Set<RDF4JBlankNodeOrIRI> getContextMask();

    /**
     * {@inheritDoc}
     * <p>
     * Note that for graphs backed by a repository ({@link #asRepository()} is
     * present), the stream <strong>must be closed</strong> with
     * {@link Stream#close()}.
     * <p>
     * This can generally achieved using a try-with-resources block, e.g.:
     *
     * <pre>
     * int subjects;
     * try (Stream&lt;RDF4JTriple&gt; s : graph.stream()) {
     *   subjects = s.map(RDF4JTriple::getSubject).distinct().count()
     * }
     * </pre>
     */
    @Override
    Stream<RDF4JTriple> stream();

    /**
     * {@inheritDoc}
     * <p>
     * Note that for graphs backed by a repository ({@link #asRepository()} is
     * present), the stream <strong>must be closed</strong> with
     * {@link Stream#close()}.
     * <p>
     * This can generally achieved using a try-with-resources block, e.g.:
     *
     * <pre>
     * int subjects;
     * try (Stream&lt;RDF4JTriple&gt; s : graph.stream(s,p,o)) {
     *   subjects = s.map(RDF4JTriple::getSubject).distinct().count()
     * }
     * </pre>
     */
    @Override
    Stream<RDF4JTriple> stream(BlankNodeOrIRI subject, IRI predicate, RDFTerm object);

    /**
     * {@inheritDoc}
     * <p>
     * Note that for graphs backed by a repository ({@link #asRepository()} is
     * present), the iterable <strong>must be closed</strong> with
     * {@link ClosableIterable#close()}.
     * <p>
     * This can generally achieved using a try-with-resources block, e.g.:
     *
     * <pre>
     * try (ClosableIterable&lt;Triple&gt; s : graph.iterate()) {
     *   for (Triple t : triples) {
     *       return t; // OK to terminate for-loop early
     *   }
     * }
     * </pre>
     *
     * If you don't use a try-with-resources block, the iterator will attempt to
     * close the ClosableIterable when reaching the end of the iteration.
     */
    @Override
    ClosableIterable<Triple> iterate() throws ConcurrentModificationException, IllegalStateException;

    /**
     * {@inheritDoc}
     * <p>
     * Note that for graphs backed by a repository ({@link #asRepository()} is
     * present), the iterable <strong>must be closed</strong> with
     * {@link ClosableIterable#close()}.
     * <p>
     * This can generally achieved using a try-with-resources block, e.g.:
     *
     * <pre>
     * try (ClosableIterable&lt;Triple&gt; s : graph.iterate(s,p,o)) {
     *   for (Triple t : triples) {
     *       return t; // OK to terminate for-loop early
     *   }
     * }
     * </pre>
     *
     * If you don't use a try-with-resources block, the iterator will attempt to
     * close the ClosableIterable when reaching the end of the iteration.
     */
    @Override
    ClosableIterable<Triple> iterate(BlankNodeOrIRI subject, IRI predicate, RDFTerm object);
}
