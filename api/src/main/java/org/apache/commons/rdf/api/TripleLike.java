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
 * A generalised "triple-like" interface, extended by {@link Triple} and {@link Quad}.
 * <p>
 * A TripleLike has at least a 
 * {@link #getSubject()}, {@link #getPredicate()} and 
 * {@link #getObject()}, but unlike a {@link Triple} does not have a
 * formalised {@link Triple#equals(Object)} semantics, and allow
 * generalised triples (e.g. a BlankNode as predicate).
 * <p>
 * Implementations should specialise which RDFTerms subclasses 
 * they return for subject, predicate and object.
 * <p>
 * @see Triple
 * @see Quad
 * @see QuadLike
 * 
 */
public interface TripleLike <S extends RDFTerm, P extends RDFTerm, O extends RDFTerm> {

    /**
     * The subject of this statement.
     *
     * @return The subject, typically an {@link IRI} or {@link BlankNode}.
     * @see <a href="http://www.w3.org/TR/rdf11-concepts/#dfn-subject">RDF-1.1
     * Triple subject</a>
     */
    S getSubject();

    /**
     * The predicate of this statement.
     *
     * @return The predicate, typically an {@link IRI}.
     * @see <a href="http://www.w3.org/TR/rdf11-concepts/#dfn-predicate">RDF-1.1
     * Triple predicate</a>
     */
    P getPredicate();

    /**
     * The object of this statement.
     *
     * @return The object, typically an {@link IRI}, {@link BlankNode} or {@link Literal}.
     * @see <a href="http://www.w3.org/TR/rdf11-concepts/#dfn-object">RDF-1.1
     * Triple object</a>
     */
    O getObject();
}
