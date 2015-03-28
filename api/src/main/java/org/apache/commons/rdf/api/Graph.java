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

import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * An <a href="http://www.w3.org/TR/rdf11-concepts/#section-rdf-graph"> RDF 1.1
 * Graph</a>, a set of RDF triples, as defined by <a
 * href="http://www.w3.org/TR/rdf11-concepts/" >RDF-1.1 Concepts and Abstract
 * Syntax</a>, a W3C Recommendation published on 25 February 2014.
 */
public interface Graph extends AutoCloseable {

	/**
	 * Add a triple to the graph.
	 *
	 * @param triple
	 *            The triple to add
	 */
	void add(Triple triple);

	/**
	 * Add a triple to the graph.
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
	 * Check if graph contains triple.
	 *
	 * @param triple
	 *            The triple to check.
	 * @return True if the Graph contains the given Triple.
	 */
	boolean contains(Triple triple);

	/**
	 * Check if graph contains a pattern of triples.
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
	 * Close the graph, relinquishing any underlying resources.
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
	 * Remove a concrete triple from the graph.
	 *
	 * @param triple
	 *            triple to remove
	 */
	void remove(Triple triple);

	/**
	 * Remove a concrete pattern of triples from the graph.
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
	 * Clear the graph, removing all triples.
	 * 
	 */
	void clear();

	/**
	 * Number of triples contained by the graph.
	 * <p>
	 * The count of a set does not include duplicates, as determined by the
	 * equals method for each {@link Triple}.
	 * 
	 * @return The number of triples in the graph
	 */
	long size();

	/**
	 * Get all triples contained by the graph.<br>
	 * <p>
	 * The iteration SHOULD NOT contain any duplicate triples, as determined by
	 * the equals method for each {@link Triple}.
	 * <p>
	 * The behaviour of the Stream is not specified if add, remove, or clear,
	 * are called on the Stream before it terminates.<br>
	 * <p>
	 * Implementations may throw ConcurrentModificationException from Stream
	 * methods if they detect a conflict while the Stream is active.
	 *
	 * @return A {@link Stream} over all of the triples in the graph
	 */
	Stream<? extends Triple> getTriples();

	/**
	 * Get all triples contained by the graph matched with the pattern.
	 * <p>
	 * The iteration does not contain any duplicate triples, as determined by
	 * the equals method for each {@link Triple}.
	 * <p>
	 * The behaviour of the Stream is not specified if add, remove, or clear,
	 * are called on the Stream before it terminates.<br>
	 * <p>
	 * Implementations may throw ConcurrentModificationException from Stream
	 * methods if they detect a conflict while the Stream is active.
	 *
	 * @param subject
	 *            The triple subject (null is a wildcard)
	 * @param predicate
	 *            The triple predicate (null is a wildcard)
	 * @param object
	 *            The triple object (null is a wildcard)
	 * @return A {@link Stream} over the matched triples.
	 */
	Stream<? extends Triple> getTriples(BlankNodeOrIRI subject, IRI predicate,
			RDFTerm object);
}
