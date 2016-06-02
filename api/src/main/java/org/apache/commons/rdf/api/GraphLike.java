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
import java.util.stream.Stream;

/**
 * A "graph-like" interface that contains {@link TripleLike} statements.
 * <p>
 * Extended by {@link Graph} (for {@link Triple}) and {@link Dataset} (for
 * {@link Quad}).
 * <p>
 * Unlike {@link Graph} and {@link Dataset}, this interface can support with
 * generalised {@link TripleLike} or {@link QuadLike} statements, and does not
 * include semantics like {@link #size()} or the requirement of mapping
 * {@link RDFTerm} instances from different implementations.
 * 
 * @see Graph
 * @see Dataset
 * @see TripleLike
 */
public interface GraphLike<T extends TripleLike<S, P, O>, S extends RDFTerm, P extends RDFTerm, O extends RDFTerm> {

	/**
	 * Add a statement.
	 * 
	 * @param statement
	 *            The TripleLike statement to add
	 */
	void add(T statement);

	/**
	 * Check if statement is contained.
	 * 
	 * @param statement
	 *            The {@link TripleLike} statement to chec
	 * @return True if the statement is contained
	 */
	boolean contains(T statement);

	/**
	 * Add a statement.
	 * 
	 * @param statement
	 *            The TripleLike statement to add
	 */
	void remove(T statement);

	/**
	 * Remove all statements.
	 */
	void clear();

	/**
	 * Number of statements.
	 * 
	 * @return
	 */
	long size();

	/**
	 * Return a Stream of contained statements.
	 * 
	 * @return A {@link Stream} of {@link TripleLike} statements.
	 */
	Stream<? extends T> stream();

	/**
	 * Iterate over contained statements.
	 * 
	 * @return An {@link Iterable} of {@link TripleLike} statements.
	 * 
	 * @throws IllegalStateException
	 *             if the {@link Iterable} has been reused
	 * @throws ConcurrentModificationException
	 *             if a concurrency conflict occurs while the Iterator is
	 *             active.
	 */
	Iterable<T> iterate() throws ConcurrentModificationException, IllegalStateException;

}
