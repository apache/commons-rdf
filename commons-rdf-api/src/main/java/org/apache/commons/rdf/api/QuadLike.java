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
 * A QuadLike statement has at least a {@link #getSubject()},
 * {@link #getPredicate()}, {@link #getObject()} and {@link #getGraphName()},
 * but unlike a {@link Quad} does not have a formalised
 * {@link Quad#equals(Object)} or {@link Quad#hashCode()} semantics and is not
 * required to be <em>immutable</em> or <em>thread-safe</em>. This interface can
 * also be used for <em>generalised quads</em> (e.g. a {@link BlankNode} as
 * predicate).
 * <p>
 * Implementations should specialise which specific {@link RDFTerm} types they
 * return by overriding {@link #getSubject()}, {@link #getPredicate()},
 * {@link #getObject()} and {@link #getGraphName()}.
 *
 * @since 0.3.0-incubating
 * @see Quad
 */
public interface QuadLike<G extends RDFTerm> extends TripleLike {

    /**
     * The graph name (graph label) of this statement, if present.
     * <p>
     * If {@link Optional#isPresent()}, then the {@link Optional#get()} indicate
     * the graph name of this statement. If the graph name is not present,e.g.
     * the value is {@link Optional#empty()}, it indicates that this Quad is in
     * the default graph.
     *
     * @return If {@link Optional#isPresent()}, the graph name of this quad,
     *         otherwise {@link Optional#empty()}, indicating the default graph.
     *         The graph name is typically an {@link IRI} or {@link BlankNode}.
     */
    Optional<G> getGraphName();

}
