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
 * Common interface for {@link Graph} and {@link Dataset}
 * <p>
 * 
 */
public interface GraphOrDataset<T extends TripleOrQuad> extends AutoCloseable {
	
    /**
     * Add a triple/quad to the graph, possibly mapping any of the components of the
     * TripleOrQuad to those supported by this Graph.
     *
     * @param tripleOrQuad The triple or quad to add
     */
    void add(T tripleOrQuad);

    /**
     * Add a triple to the Graph, (or the default graph of a Dataset), 
     * possibly mapping any of the components to
     * those supported by this graph/dataset.
     *
     * @param subject   The triple subject
     * @param predicate The triple predicate
     * @param object    The triple object
     */
    void add(BlankNodeOrIRI subject, IRI predicate, RDFTerm object);

    /**
     * Check if graph/dataset contains triple/quad.
     *
     * @param tripleOrQuad The triple/quad to check.
     * @return True if this graph/dataset contains the given Triple.
     */
    boolean contains(T tripleOrQuad);

    /**
	 * Check if graph/dataset contains a pattern of triples or quads in the
	 * default graph of a dataset.
	 *
	 * @param subject
	 *            The triple subject (null is a wildcard)
	 * @param predicate
	 *            The triple predicate (null is a wildcard)
	 * @param object
	 *            The triple object (null is a wildcard)
	 * @return True if this graph/dataset contains any triples/quads 
	 * 	that match the given pattern.
	 */
    boolean contains(BlankNodeOrIRI subject, IRI predicate, RDFTerm object);

    /**
     * Close the graph/dataset, relinquishing any underlying resources.
     * <p>
     * For example, this would close any open file and network streams and free
     * database locks held by the Graph implementation.
     * <p>
     * The behaviour of the other Graph methods are undefined after closing the
     * graph.
     * <p>
     * Implementations might not need {@link #close()}, hence the default
     * implementation does nothing.
     */
    @Override
    default void close() throws Exception {
    }

    /**
     * Remove a concrete triple/quad from the graph.
     *
     * @param tripleOrQuad triple/quad to remove
     */
    void remove(T tripleOrQuad);

    /**
     * Remove a concrete pattern of triples from the graph, or
     * quads from the default graph of a dataset.
     *
     * @param subject   The triple subject (null is a wildcard)
     * @param predicate The triple predicate (null is a wildcard)
     * @param object    The triple object (null is a wildcard)
     */
    void remove(BlankNodeOrIRI subject, IRI predicate, RDFTerm object);

    /**
     * Clear the graph/dataset, removing all triples/quads.
     */
    void clear();

    /**
     * Number of triples/quads contained by the graph/dataset.
     *
     * @return The number of triples/quads in the graph/dataset
     */
    long size();

    /**
     * Get all triples contained by the graph, or 
     * the equivalent of {@link Quad#asTriple()} 
     * for all quads of the default graph of a dataset.
     * <p>
     * The iteration does not contain any duplicate triples, as determined by
     * the {@link Triple#equals(Object)} method for each {@link Triple}.
     * <p>
     * The behaviour of the {@link Stream} is not specified if {@link #add(TripleOrQuad)},
     * {@link #remove(TripleOrQuad)} or {@link #clear()} are called on the
     * {@link GraphOrDataset} before it terminates.
     * <p>
     * Implementations may throw {@link ConcurrentModificationException} from Stream
     * methods if they detect a conflict while the Stream is active.
     *
     * @return A {@link Stream} over all of the triples in the graph
     */
    Stream<? extends Triple> getTriples();

    /**
     * Get all triples contained by the graph matched with the pattern, or
     * the equivalent of {@link Quad#asTriple()} 
     * for all quads of the default graph of a dataset that match the pattern.
     * <p>
     * The iteration does not contain any duplicate triples, as determined by
     * the {@link Triple#equals(Object)} method for each {@link Triple}.
     * <p>
     * The behaviour of the {@link Stream} is not specified if {@link #add(TripleOrQuad)},
     * {@link #remove(TripleOrQuad)} or {@link #clear()} are called on the
     * {@link GraphOrDataset} before it terminates.
     * <p>
     * Implementations may throw {@link ConcurrentModificationException} from Stream
     * methods if they detect a conflict while the Stream is active.
     *
     * @param subject   The triple subject (null is a wildcard)
     * @param predicate The triple predicate (null is a wildcard)
     * @param object    The triple object (null is a wildcard)
     * @return A {@link Stream} over the matched triples.
     */
    Stream<? extends Triple> getTriples(BlankNodeOrIRI subject, IRI predicate,
                                        RDFTerm object);

    /**
     * Get an Iterable for iterating over all triples in the graph.
     * <p>
     * This method is meant to be used with a Java for-each loop, e.g.:
     * <pre>
     *  for (TripleOrQuad t : graphOrDataset.iterate()) {
     *      System.out.println(t);
     *  }
     * </pre>
     * The behaviour of the iterator is not specified if {@link #add(TripleOrQuad)},
     * {@link #remove(TripleOrQuad)} or {@link #clear()}, are called on the
     * {@link GraphOrDataset} before it terminates. It is undefined if the returned
     * {@link Iterator} supports the {@link Iterator#remove()} method.
     * <p>
     * Implementations may throw {@link ConcurrentModificationException} from
     * Iterator methods if they detect a concurrency conflict while the Iterator
     * is active.
     * <p>
     * The {@link Iterable#iterator()} must only be called once, that is the
     * Iterable must only be iterated over once. A {@link IllegalStateException}
     * may be thrown on attempt to reuse the Iterable.
     *
     * @return A {@link Iterable} that returns {@link Iterator} over all of the
     *         triples in the graph
     * @throws IllegalStateException
     *             if the {@link Iterable} has been reused
     * @throws ConcurrentModificationException
     *             if a concurrency conflict occurs while the Iterator is
     *             active.
     */
    @SuppressWarnings("unchecked")
    default Iterable<T> iterate()
            throws ConcurrentModificationException, IllegalStateException {
        return ((Stream<T>)getTriples())::iterator;
    }

    /**
     * Get an Iterable for iterating over the triples in the graph 
     * or quads in the default graph of a dataset that  
     * match the pattern.
     * <p>
     * This method is meant to be used with a Java for-each loop, e.g.:
     * <pre>
     *  IRI alice = factory.createIRI("http://example.com/alice");
     *  IRI knows = factory.createIRI("http://xmlns.com/foaf/0.1/");
     *  for (Triple t : graphOrDataset.iterate(alice, knows, null)) {
     *      System.out.println(t.getObject());
     *  }
     * </pre>
     * <p>
     * The behaviour of the iterator is not specified if
     * {@link #add(TripleOrQuad)}, {@link #remove(TripleOrQuad)} or {@link #clear()}, are
     * called on the {@link GraphOrDataset} before it terminates. It is undefined if the
     * returned {@link Iterator} supports the {@link Iterator#remove()} method.
     * <p>
     * Implementations may throw {@link ConcurrentModificationException} from
     * Iterator methods if they detect a concurrency conflict while the Iterator
     * is active.
     * <p>
     * The {@link Iterable#iterator()} must only be called once, that is the
     * Iterable must only be iterated over once. A {@link IllegalStateException}
     * may be thrown on attempt to reuse the Iterable.
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
    default Iterable<T> iterate(
            BlankNodeOrIRI subject, IRI predicate, RDFTerm object)
        throws ConcurrentModificationException, IllegalStateException {
        return ((Stream<T>) getTriples(subject, predicate, object))::iterator;
    }
}
