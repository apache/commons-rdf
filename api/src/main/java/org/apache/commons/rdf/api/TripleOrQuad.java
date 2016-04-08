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

/**
 * Common "triple-like" interface for {@link Triple} and {@link Quad}
 * <p>
 * A TripleOrQuad has at least a 
 * {@link #getSubject()}, {@link #getPredicate()} and 
 * {@link #getObject()}, but unlike a {@link Triple} does not have a
 * formalised {@link Triple#equals(Object)} semantics, and does not 
 * necessarily have a {@link Quad#getGraphName()}
 * <p>
 * Implementations of this interface SHOULD also implement {@link Triple}
 * or {@link Quad}, but MUST NOT implement both interfaces.
 */
public interface TripleOrQuad {

    /**
     * The subject of this triple/quad, which may be either a {@link BlankNode} or an
     * {@link IRI}, which are represented in Commons RDF by the interface
     * {@link BlankNodeOrIRI}.
     *
     * @return The subject {@link BlankNodeOrIRI} of this triple/quad.
     * @see <a href="http://www.w3.org/TR/rdf11-concepts/#dfn-subject">RDF-1.1
     * Triple subject</a>
     */
    BlankNodeOrIRI getSubject();

    /**
     * The predicate {@link IRI} of this triple/quad.
     *
     * @return The predicate {@link IRI} of this triple/quad.
     * @see <a href="http://www.w3.org/TR/rdf11-concepts/#dfn-predicate">RDF-1.1
     * Triple predicate</a>
     */
    IRI getPredicate();

    /**
     * The object of this triple/quad, which may be either a {@link BlankNode}, an
     * {@link IRI}, or a {@link Literal}, which are represented in Commons RDF
     * by the interface {@link RDFTerm}.
     *
     * @return The object {@link RDFTerm} of this triple/quad.
     * @see <a href="http://www.w3.org/TR/rdf11-concepts/#dfn-object">RDF-1.1
     * Triple object</a>
     */
    RDFTerm getObject();
}
