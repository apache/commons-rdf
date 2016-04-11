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
     * Check if graph/dataset contains triple/quad.
     *
     * @param tripleOrQuad The triple/quad to check.
     * @return True if this graph/dataset contains the given Triple.
     */
    boolean contains(T tripleOrQuad);

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
     * Get an Iterable for iterating over all triples/quads in the graph/dataset.
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
    Iterable<T> iterate()
            throws ConcurrentModificationException, IllegalStateException;

}
