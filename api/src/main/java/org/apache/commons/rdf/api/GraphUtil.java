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

import java.util.Iterator;
import java.util.stream.Stream;

/**
 * Utility functions for working with {@link Graph}s.
 *
 */
public class GraphUtil {
    /**
     * Get an Iterable for iterating over all triples in the graph.
     * <p>
     * This method is meant to be used with a classical Java for-each loop, e.g.:
     * <code>
     *  for (Triple t : GraphUtil.iterate(graph)) {
            System.out.println(t);
        }
     * </code>
     * <p>
     * The behaviour of the iterator is not specified if add, remove, or clear,
     * are called on the Graph before it terminates. It is undefined if the
     * returned Iterator supports the remove method.
     * <p>
     * Implementations may throw ConcurrentModificationException from Iterator
     * methods if they detect a concurrency conflict while the Iterator is
     * active.
     *
     * @param graph The {@link Graph} which triples are to be iterated over
     * @return A {@link Iterable} that returns {@link Iterator} over all of the triples in the graph
     * 
     */  
    public static Iterable<Triple> iterate(final Graph graph) {
        return new Iterable<Triple>() {            
            @SuppressWarnings("unchecked")
            @Override
            public Iterator<Triple> iterator() {
                return (Iterator<Triple>) graph.getTriples().iterator();
            }
        };
    }

    /**
     * Get an Iterable for iterating over all triples in the Stream of Triples.
     * <p>
     * This method is meant to be used with a classical Java for-each loop
     * together with {@link Graph#getTriples(BlankNodeOrIRI, IRI, RDFTerm)},
     * e.g.: <code>
     *  IRI s1, p1; // ...   
     *  for (Triple t : GraphUtil.iterate(graph.getTriples(s1, p1, null)) {
     *      System.out.println(t.getObject());
     *  }
     * </code>
     * <p>
     * The behaviour of the iterator is not specified if add, remove, or clear,
     * are called on the Graph before it terminates. It is undefined if the
     * returned Iterator supports the remove method.
     * <p>
     * Implementations may throw ConcurrentModificationException from Iterator
     * methods if they detect a concurrency conflict while the Iterator is
     * active.
     * <p>
     * As {@link Stream#iterator()} is a terminal operation, this method should
     * only be used once on a stream, and its {@link Iterable#iterator()} must
     * only be iterated over once. A IllegalStateException is thrown on attempt
     * to reuse a stream or Iterable.
     *
     * @param graph
     *            The {@link Graph} which triples are to be iterated over
     * @return A {@link Iterable} that returns {@link Iterator} over all of the
     *         triples in the graph
     * @throws IllegalStateException
     *             if the {@link Stream} or the {@link Iterable} has been reused
     */      
    public static Iterable<Triple> iterate(Stream<? extends Triple> triples) throws IllegalStateException {
        return new Iterable<Triple>() {           
            @SuppressWarnings("unchecked")
            @Override
            public Iterator<Triple> iterator() {
                return (Iterator<Triple>) triples.iterator();
            }
        };
    }
}
