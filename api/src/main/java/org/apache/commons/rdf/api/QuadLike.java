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

import java.util.Optional;

/**
 * A generalised "quad-like" interface, extended by {@link Quad}.
 * <p>
 * A QuadLike has at least a 
 * {@link #getSubject()}, {@link #getPredicate()} and 
 * {@link #getObject()}, and a {@link #getGraphName()}, 
 * but unlike a {@link Quad} does not have a
 * formalised {@link Quad#equals(Object)} semantics, and allow
 * generalised quads (e.g. a BlankNode as predicate).
 * <p>
 * Implementations should specialise which RDFTerm 
 * subclasses they return for subject {@link S}, 
 * predicate {@link P}, object {@link O} and graph name {@link G}.
 * <p>
 * @see Quad
 */
public interface QuadLike <S extends RDFTerm, P extends RDFTerm, O extends RDFTerm, G extends RDFTerm> 
	extends TripleLike<S,P,O> {


	/**
	 * The graph name (graph label) of this quad, if present.
	 * 
	 * If {@link Optional#isPresent()}, then the {@link Optional#get()} 
	 * indicate the
	 * <a href="https://www.w3.org/TR/rdf11-concepts/#dfn-named-graph">graph
	 * name of this Quad. If the graph name is not present (e.g. the value is
	 * {@link Optional#empty()}), it indicates that this Quad is in the
	 * <a href="https://www.w3.org/TR/rdf11-concepts/#dfn-default-graph">default
	 * graph.
	 *
	 * @return If {@link Optional#isPresent()}, the graph name
	 *         of this quad, otherwise. The graph name is typically an
	 *         {@link IRI} or {@link BlankNode}.
	 *         {@link Optional#empty()}, indicating the default graph.
	 * 
	 * @see <a href="https://www.w3.org/TR/rdf11-concepts/#dfn-rdf-dataset">RDF-
	 *      1.1 Dataset</a>
	 */
	Optional<G> getGraphName();
	
}
