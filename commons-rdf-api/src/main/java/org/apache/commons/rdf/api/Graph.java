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
package org.apache.commons.rdf.api;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * An <a href="http://www.w3.org/TR/rdf11-concepts/#section-rdf-graph"> RDF 1.1
 * Graph</a>, a set of RDF triples, as defined by
 * <a href="http://www.w3.org/TR/rdf11-concepts/" >RDF-1.1 Concepts and Abstract
 * Syntax</a>, a W3C Recommendation published on 25 February 2014.
 *
 * @see RDF#createGraph()
 */
public interface Graph extends AutoCloseable, GraphLike<Triple> {

    /**
     * Adds a triple to the graph, possibly mapping any of the components of the
     * Triple to those supported by this Graph.
     *
     * @param triple
     *            The triple to add
     */
    @Override
    void add(Triple triple);

    /**
     * Adds a triple to the graph, possibly mapping any of the components to
     * those supported by this Graph.
     *
     * @param subject
     *            The triple subject
     * @param predicate
     *            The triple predicate
     * @param object
     *            The triple object
     */
    void add(BlankNodeOrIRI subject, IRI predicate, RDFTerm object);

    /**
     * Checks if graph contains triple.
     *
     * @param triple
     *            The triple to check.
     * @return True if the Graph contains the given Triple.
     */
    @Override
    boolean contains(Triple triple);

    /**
     * Checks if graph contains a pattern of triples.
     *
     * @param subject
     *            The triple subject (null is a wildcard)
     * @param predicate
     *            The triple predicate (null is a wildcard)
     * @param object
     *            The triple object (null is a wildcard)
     * @return True if the Graph contains any Triples that match the given
     *         pattern.
     */
    boolean contains(BlankNodeOrIRI subject, IRI predicate, RDFTerm object);

    /**
     * Closes the graph, relinquishing any underlying resources.
     * <p>
     * For example, this would close any open file and network streams and free
     * database locks held by the Graph implementation.
     * <p>
     * The behavior of the other Graph methods are undefined after closing the
     * graph.
     * <p>
     * Implementations might not need {@link #close()}, hence the default
     * implementation does nothing.
     */
    @Override
    default void close() throws Exception {
    }

    /**
     * Removes a concrete triple from the graph.
     *
     * @param triple
     *            triple to remove
     */
    @Override
    void remove(Triple triple);

    /**
     * Removes a concrete pattern of triples from the graph.
     *
     * @param subject
     *            The triple subject (null is a wildcard)
     * @param predicate
     *            The triple predicate (null is a wildcard)
     * @param object
     *            The triple object (null is a wildcard)
     */
    void remove(BlankNodeOrIRI subject, IRI predicate, RDFTerm object);

    /**
     * Clears the graph, removing all triples.
     */
    @Override
    void clear();

    /**
     * Number of triples contained by the graph.
     * <p>
     * The count of a set does not include duplicates, consistent with the
     * {@link Triple#equals(Object)} equals method for each {@link Triple}.
     *
     * @return The number of triples in the graph
     */
    @Override
    long size();

    /**
     * Gets all triples contained by the graph.<br>
     * <p>
     * The iteration does not contain any duplicate triples, as determined by
     * the {@link Triple#equals(Object)} method for each {@link Triple}.
     * <p>
     * The behavior of the {@link Stream} is not specified if
     * {@link #add(Triple)}, {@link #remove(Triple)} or {@link #clear()} are
     * called on the {@link Graph} before it terminates.
     * <p>
     * Implementations may throw {@link ConcurrentModificationException} from
     * Stream methods if they detect a conflict while the Stream is active.
     *
     * @since 0.3.0-incubating
     * @return A {@link Stream} over all of the triples in the graph
     */
    @Override
    Stream<? extends Triple> stream();

    /**
     * Gets all triples contained by the graph matched with the pattern.
     * <p>
     * The iteration does not contain any duplicate triples, as determined by
     * the {@link Triple#equals(Object)} method for each {@link Triple}.
     * <p>
     * The behavior of the {@link Stream} is not specified if
     * {@link #add(Triple)}, {@link #remove(Triple)} or {@link #clear()} are
     * called on the {@link Graph} before it terminates.
     * <p>
     * Implementations may throw {@link ConcurrentModificationException} from
     * Stream methods if they detect a conflict while the Stream is active.
     * <p>
     *
     * @since 0.3.0-incubating
     * @param subject
     *            The triple subject (null is a wildcard)
     * @param predicate
     *            The triple predicate (null is a wildcard)
     * @param object
     *            The triple object (null is a wildcard)
     * @return A {@link Stream} over the matched triples.
     */
    Stream<? extends Triple> stream(BlankNodeOrIRI subject, IRI predicate, RDFTerm object);

    /**
     * This method is deprecated, use the equivalent method {@link #stream()}
     * instead.
     *
     * @return A {@link Stream} over all triples.
     */
    @Deprecated
    default Stream<? extends Triple> getTriples() {
        return stream();
    }

    /**
     * This method is deprecated, use the equivalent method
     * {@link #stream(BlankNodeOrIRI, IRI, RDFTerm)} instead.
     *
     * @param subject
     *            The triple subject (null is a wildcard)
     * @param predicate
     *            The triple predicate (null is a wildcard)
     * @param object
     *            The triple object (null is a wildcard)
     * @return A {@link Stream} over the matched triples.
     */
    @Deprecated
    default Stream<? extends Triple> getTriples(final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object) {
        return stream(subject, predicate, object);
    }

    /**
     * Tests if this is empty.
     *
     * @return true if this is empty.
     */
    default boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Gets an Iterable for iterating over all triples in the graph.
     * <p>
     * This method is meant to be used with a Java for-each loop, e.g.:
     *
     * <pre>
     * for (Triple t : graph.iterate()) {
     *     System.out.println(t);
     * }
     * </pre>
     *
     * The behavior of the iterator is not specified if {@link #add(Triple)},
     * {@link #remove(Triple)} or {@link #clear()}, are called on the
     * {@link Graph} before it terminates. It is undefined if the returned
     * {@link Iterator} supports the {@link Iterator#remove()} method.
     * <p>
     * Implementations may throw {@link ConcurrentModificationException} from
     * Iterator methods if they detect a concurrency conflict while the Iterator
     * is active.
     * <p>
     * The {@link Iterable#iterator()} must only be called once, that is the
     * Iterable must only be iterated over once. A {@link IllegalStateException}
     * may be thrown on attempt to reuse the Iterable.
     * <p>
     * The default implementation of this method will call {@link #stream()} to return
     * its {@link Stream#iterator()}.
     *
     * @return A {@link Iterable} that returns {@link Iterator} over all of the
     *         triples in the graph
     * @throws IllegalStateException
     *             if the {@link Iterable} has been reused
     * @throws ConcurrentModificationException
     *             if a concurrency conflict occurs while the Iterator is
     *             active.
     */
    @Override
    @SuppressWarnings("unchecked")
    default Iterable<Triple> iterate() throws ConcurrentModificationException, IllegalStateException {
        return ((Stream<Triple>) stream())::iterator;
    }

    /**
     * Gets an Iterable for iterating over the triples in the graph that match
     * the pattern.
     * <p>
     * This method is meant to be used with a Java for-each loop, e.g.:
     *
     * <pre>
     * IRI alice = factory.createIRI("http://example.com/alice");
     * IRI knows = factory.createIRI("http://xmlns.com/foaf/0.1/");
     * for (Triple t : graph.iterate(alice, knows, null)) {
     *     System.out.println(t.getObject());
     * }
     * </pre>
     * <p>
     * The behavior of the iterator is not specified if {@link #add(Triple)},
     * {@link #remove(Triple)} or {@link #clear()}, are called on the
     * {@link Graph} before it terminates. It is undefined if the returned
     * {@link Iterator} supports the {@link Iterator#remove()} method.
     * <p>
     * Implementations may throw {@link ConcurrentModificationException} from
     * Iterator methods if they detect a concurrency conflict while the Iterator
     * is active.
     * <p>
     * The {@link Iterable#iterator()} must only be called once, that is the
     * Iterable must only be iterated over once. A {@link IllegalStateException}
     * may be thrown on attempt to reuse the Iterable.
     * <p>
     * The default implementation of this method will call
     * {@link #stream(BlankNodeOrIRI, IRI, RDFTerm)} to return its
     * {@link Stream#iterator()}.
     *
     * @param subject
     *            The triple subject (null is a wildcard)
     * @param predicate
     *            The triple predicate (null is a wildcard)
     * @param object
     *            The triple object (null is a wildcard)
     * @return A {@link Iterable} that returns {@link Iterator} over the
     *         matching triples in the graph
     * @throws IllegalStateException
     *             if the {@link Iterable} has been reused
     * @throws ConcurrentModificationException
     *             if a concurrency conflict occurs while the Iterator is
     *             active.
     */
    @SuppressWarnings("unchecked")
    default Iterable<Triple> iterate(final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object)
            throws ConcurrentModificationException, IllegalStateException {
        return ((Stream<Triple>) stream(subject, predicate, object))::iterator;
    }

}
