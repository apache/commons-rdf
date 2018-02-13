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

import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Quad;
import org.apache.commons.rdf.api.RDFTerm;

/**
 * Marker interface for RDF4J implementations of Dataset.
 *
 * @see RDF4J#createDataset()
 * @see RDF4J#asDataset(org.eclipse.rdf4j.repository.Repository, Option...)
 */
public interface RDF4JDataset extends Dataset, RDF4JGraphLike<Quad> {

    /**
     * {@inheritDoc}
     * <p>
     * Note that for datasets backed by a repository ({@link #asRepository()} is
     * present), the stream <strong>must be closed</strong> with
     * {@link Stream#close()}.
     * <p>
     * This can generally achieved using a try-with-resources block, e.g.:
     *
     * <pre>
     * int subjects;
     * try (Stream&lt;RDF4JQuad&gt; s : graph.stream()) {
     *   subjects = s.map(RDF4JQuad::getSubject).distinct().count()
     * }
     * </pre>
     */
    @Override
    Stream<RDF4JQuad> stream();

    /**
     * {@inheritDoc}
     * <p>
     * Note that for datasets backed by a repository ({@link #asRepository()} is
     * present), the stream <strong>must be closed</strong> with
     * {@link Stream#close()}.
     * <p>
     * This can generally achieved using a try-with-resources block, e.g.:
     *
     * <pre>
     * int subjects;
     * try (Stream&lt;RDF4JQuad&gt; s : graph.stream()) {
     *   subjects = s.map(RDF4JQuad::getSubject).distinct().count()
     * }
     * </pre>
     */
    @Override
    Stream<RDF4JQuad> stream(Optional<BlankNodeOrIRI> graphName, BlankNodeOrIRI subject, IRI predicate, RDFTerm object);

    /**
     * {@inheritDoc}
     * <p>
     * Note that for datasets backed by a repository ({@link #asRepository()} is
     * present), the stream <strong>must be closed</strong> with
     * {@link Stream#close()}.
     * <p>
     * This can generally achieved using a try-with-resources block, e.g.:
     *
     * <pre>
     * int graphs;
     * try (Stream&lt;BlankNodeOrIRI&gt; s : graph.stream()) {
     *   graphs = s.count()
     * }
     * </pre>
     */
    @Override
    Stream<BlankNodeOrIRI> getGraphNames();

    /**
     * {@inheritDoc}
     * <p>
     * Note that for datasets backed by a repository ({@link #asRepository()} is
     * present), the iterable <strong>must be closed</strong> with
     * {@link ClosableIterable#close()}.
     * <p>
     * This can generally achieved using a try-with-resources block, e.g.:
     *
     * <pre>
     * try (ClosableIterable&lt;Quad&gt; s : graph.iterate()) {
     *   for (Quad q : quads) {
     *       return q; // OK to terminate for-loop early
     *   }
     * }
     * </pre>
     *
     * If you don't use a try-with-resources block, the iterator will attempt to
     * close the ClosableIterable when reaching the end of the iteration.
     */
    @Override
    ClosableIterable<Quad> iterate();

    /**
     * {@inheritDoc}
     * <p>
     * Note that for datasets backed by a repository ({@link #asRepository()} is
     * present), the iterable <strong>must be closed</strong> with
     * {@link ClosableIterable#close()}.
     * <p>
     * This can generally achieved using a try-with-resources block, e.g.:
     *
     * <pre>
     * try (ClosableIterable&lt;Quad&gt; s : graph.iterate(g,s,p,o)) {
     *   for (Quad q : quads) {
     *       return q; // OK to terminate for-loop early
     *   }
     * }
     * </pre>
     *
     * If you don't use a try-with-resources block, the iterator will attempt to
     * close the ClosableIterable when reaching the end of the iteration.
     */
    @Override
    ClosableIterable<Quad> iterate(Optional<BlankNodeOrIRI> graphName, BlankNodeOrIRI subject, IRI predicate,
            RDFTerm object);

}
