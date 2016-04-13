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
import java.util.Optional;
import java.util.stream.Stream;

/**
 * An <a href="http://www.w3.org/TR/rdf11-concepts/#section-rdf-dataset"> RDF
 * 1.1 dataset</a>, a set of RDF quads, as defined by
 * <a href="http://www.w3.org/TR/rdf11-concepts/" >RDF-1.1 Concepts and Abstract
 * Syntax</a>, a W3C Recommendation published on 25 February 2014.
 */
public interface Dataset extends AutoCloseable {

	/**
	 * Add a quad to the dataset, possibly mapping any of the components of the
	 * Quad to those supported by this dataset.
	 *
	 * @param quad
	 *            The quad to add
	 */
	void add(Quad quad);

	/**
	 * Add a quad to the dataset, possibly mapping any of the components to
	 * those supported by this dataset.
	 *
	 * @param graphName
	 *            The graph the quad belongs to, or <code>null</code> for the
	 *            default graph
	 * @param subject
	 *            The quad subject
	 * @param predicate
	 *            The quad predicate
	 * @param object
	 *            The quad object
	 */
	void add(BlankNodeOrIRI graphName, BlankNodeOrIRI subject, IRI predicate, RDFTerm object);

	/**
	 * Check if dataset contains quad.
	 *
	 * @param quad
	 *            The quad to check.
	 * @return True if the dataset contains the given Quad.
	 */
	boolean contains(Quad quad);

	/**
	 * Check if dataset contains a pattern of quads.
	 *
	 * @param graphName
	 *            The graph the quad belongs to, wrapped as an {@link Optional}
	 *            (<code>null</code> is a wildcard, {@link Optional#empty()} is
	 *            the default graph)
	 * @param subject
	 *            The quad subject (<code>null</code> is a wildcard)
	 * @param predicate
	 *            The quad predicate (<code>null</code> is a wildcard)
	 * @param object
	 *            The quad object (<code>null</code> is a wildcard)
	 * @return True if the dataset contains any quads that match the given
	 *         pattern.
	 */
	boolean contains(Optional<BlankNodeOrIRI> graphName, BlankNodeOrIRI subject, IRI predicate, RDFTerm object);

	/**
	 * Close the dataset, relinquishing any underlying resources.
	 * <p>
	 * For example, this would close any open file and network streams and free
	 * database locks held by the dataset implementation.
	 * <p>
	 * The behaviour of the other dataset methods are undefined after closing
	 * the dataset.
	 * <p>
	 * Implementations might not need {@link #close()}, hence the default
	 * implementation does nothing.
	 */
	@Override
	default void close() throws Exception {
	}

	/**
	 * Get the default graph of this dataset.
	 * <p>
	 * The {@link Triple}s of the default graph are equivalent to the
	 * {@link Quad}s in this Dataset which has the {@link Quad#getGraphName()}
	 * set to {@link Optional#empty()}.
	 * <p>
	 * It is unspecified if modifications to the returned Graph are reflected in
	 * this Dataset.
	 * <p>
	 * The returned graph MAY be empty.
	 * 
	 * @see #getGraph(BlankNodeOrIRI)
	 * @return The default graph of this Dataset
	 */
	Graph getGraph();
	
	/**
	 * Get a named graph in this dataset.
	 * <p>
	 * The {@link Triple}s of the named graph are equivalent to the
	 * the Quads of this Dataset which has the
	 * {@link Quad#getGraphName()} equal to the provided <code>graphName</code>, or
	 * equal to {@link Optional#empty()} if the provided <code>graphName</code> is
	 * <code>null</code>.
	 * <p>
	 * It is unspecified if modifications to the returned Graph are reflected in
	 * this Dataset.
	 * <p>
	 * It is unspecified if requesting an unknown or empty graph will return
	 * {@link Optional#empty()} or create a new empty {@link Graph}.
	 * 
	 * @see #getGraph()
	 * @see #getGraphNames()
	 * @param graphName
	 *            The name of the graph, or <code>null</code> for the default
	 *            graph.
	 * @return The named Graph, or {@link Optional#empty()} if the dataset do
	 *         not contain the named graph.
	 */	
	Optional<Graph> getGraph(BlankNodeOrIRI graphName);	
	
	/**
	 * Get the graph names in this Dataset.
	 * <p>
	 * The set of returned graph names is equivalent to the set of unique
	 * {@link Quad#getGraphName()} of all the {@link #getQuads()} of this
	 * dataset (excluding the default graph).
	 * <p>
	 * The returned {@link Stream} SHOULD NOT contain duplicate graph names.
	 * <p>
	 * The graph names can be used with {@link #getGraph(BlankNodeOrIRI)} to
	 * retrieve the corresponding {@link Graph}, however callers should be aware
	 * of any concurrent modifications to the Dataset may cause such calls to
	 * return {@link Optional#empty()}.
	 * <p>
	 * Note that a Dataset always contains a <strong>default graph</strong>
	 * which is not named, and thus is not represented in the returned Stream.
	 * The default graph is accessible via {@link #getGraph()} or by using
	 * {@link Optional#empty()} in the Quad access methods).
	 * 
	 * @return A {@link Stream} of the graph names of this Dataset.
	 */
	Stream<BlankNodeOrIRI> getGraphNames();
	
	/**
	 * Remove a concrete quad from the dataset.
	 *
	 * @param quad
	 *            quad to remove
	 */
	void remove(Quad quad);

	/**
	 * Remove a concrete pattern of quads from the default graph of the dataset.
	 *
	 * @param graphName
	 *            The graph the quad belongs to, wrapped as an {@link Optional}
	 *            (<code>null</code> is a wildcard, {@link Optional#empty()} is
	 *            the default graph)
	 * @param subject
	 *            The quad subject (<code>null</code> is a wildcard)
	 * @param predicate
	 *            The quad predicate (<code>null</code> is a wildcard)
	 * @param object
	 *            The quad object (<code>null</code> is a wildcard)
	 */
	void remove(Optional<BlankNodeOrIRI> graphName, BlankNodeOrIRI subject, IRI predicate, RDFTerm object);

	/**
	 * Clear the dataset, removing all quads.
	 */
	void clear();

	/**
	 * Number of quads contained by the dataset.
	 * <p>
	 * The count of a set does not include duplicates, consistent with the
	 * {@link Quad#equals(Object)} equals method for each {@link Quad}.
	 *
	 * @return The number of quads in the dataset
	 */
	long size();

	/**
	 * Get all quads contained by the dataset.<br>
	 * <p>
	 * The iteration does not contain any duplicate quads, as determined by the
	 * {@link Quad#equals(Object)} method for each {@link Quad}.
	 * <p>
	 * The behaviour of the {@link Stream} is not specified if
	 * {@link #add(Quad)}, {@link #remove(Quad)} or {@link #clear()} are called
	 * on the {@link Dataset} before it terminates.
	 * <p>
	 * Implementations may throw {@link ConcurrentModificationException} from
	 * Stream methods if they detect a conflict while the Stream is active.
	 *
	 * @return A {@link Stream} over all of the quads in the dataset
	 */
	Stream<? extends Quad> getQuads();

	/**
	 * Get all quads contained by the dataset matched with the pattern.
	 * <p>
	 * The iteration does not contain any duplicate quads, as determined by the
	 * {@link Quad#equals(Object)} method for each {@link Quad}.
	 * <p>
	 * The behaviour of the {@link Stream} is not specified if
	 * {@link #add(Quad)}, {@link #remove(Quad)} or {@link #clear()} are called
	 * on the {@link Dataset} before it terminates.
	 * <p>
	 * Implementations may throw {@link ConcurrentModificationException} from
	 * Stream methods if they detect a conflict while the Stream is active.
	 *
	 * @param graphName
	 *            The graph the quad belongs to, wrapped as an {@link Optional}
	 *            (<code>null</code> is a wildcard, {@link Optional#empty()} is
	 *            the default graph)
	 * @param subject
	 *            The quad subject (<code>null</code> is a wildcard)
	 * @param predicate
	 *            The quad predicate (<code>null</code> is a wildcard)
	 * @param object
	 *            The quad object (<code>null</code> is a wildcard)
	 * @return A {@link Stream} over the matched quads.
	 */
	Stream<? extends Quad> getQuads(Optional<BlankNodeOrIRI> graphName, BlankNodeOrIRI subject, IRI predicate,
			RDFTerm object);

	/**
	 * Get an Iterable for iterating over all quads in the dataset.
	 * <p>
	 * This method is meant to be used with a Java for-each loop, e.g.:
	 * 
	 * <pre>
	 * for (Quad t : dataset.iterate()) {
	 * 	System.out.println(t);
	 * }
	 * </pre>
	 * 
	 * The behaviour of the iterator is not specified if {@link #add(Quad)},
	 * {@link #remove(Quad)} or {@link #clear()}, are called on the
	 * {@link Dataset} before it terminates. It is undefined if the returned
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
	 *         quads in the dataset
	 * @throws IllegalStateException
	 *             if the {@link Iterable} has been reused
	 * @throws ConcurrentModificationException
	 *             if a concurrency conflict occurs while the Iterator is
	 *             active.
	 */
	@SuppressWarnings("unchecked")
	default Iterable<Quad> iterate() throws ConcurrentModificationException, IllegalStateException {
		return ((Stream<Quad>) getQuads())::iterator;
	}

	/**
	 * Get an Iterable for iterating over the quads in the dataset that match
	 * the pattern.
	 * <p>
	 * This method is meant to be used with a Java for-each loop, e.g.:
	 * 
	 * <pre>
	 * IRI alice = factory.createIRI("http://example.com/alice");
	 * IRI knows = factory.createIRI("http://xmlns.com/foaf/0.1/");
	 * for (Quad t : dataset.iterate(null, alice, knows, null)) {
	 * 	   System.out.println(t.getGraphName()); 
	 *     System.out.println(t.getObject());
	 * }
	 * </pre>
	 * <p>
	 * The behaviour of the iterator is not specified if {@link #add(Quad)},
	 * {@link #remove(Quad)} or {@link #clear()}, are called on the
	 * {@link Dataset} before it terminates. It is undefined if the returned
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
	 * @param graphName
	 *            The graph the quad belongs to, wrapped as an {@link Optional}
	 *            (<code>null</code> is a wildcard, {@link Optional#empty()} is
	 *            the default graph)
	 * @param subject
	 *            The quad subject (<code>null</code> is a wildcard)
	 * @param predicate
	 *            The quad predicate (<code>null</code> is a wildcard)
	 * @param object
	 *            The quad object (<code>null</code> is a wildcard)
	 * @return A {@link Iterable} that returns {@link Iterator} over the
	 *         matching quads in the dataset
	 * @throws IllegalStateException
	 *             if the {@link Iterable} has been reused
	 * @throws ConcurrentModificationException
	 *             if a concurrency conflict occurs while the Iterator is
	 *             active.
	 */
	@SuppressWarnings("unchecked")
	default Iterable<Quad> iterate(Optional<BlankNodeOrIRI> graphName, BlankNodeOrIRI subject, IRI predicate,
			RDFTerm object) throws ConcurrentModificationException, IllegalStateException {
		return ((Stream<Quad>) getQuads(graphName, subject, predicate, object))::iterator;
	}
}
