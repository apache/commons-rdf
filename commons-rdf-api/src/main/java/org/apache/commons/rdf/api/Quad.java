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

import java.util.Objects;
import java.util.Optional;

/**
 * A Quad is a statement in a
 * <a href= "http://www.w3.org/TR/rdf11-concepts/#section-dataset" >RDF-1.1
 * Dataset</a>, as defined by <a href=
 * "https://www.w3.org/TR/2014/NOTE-rdf11-datasets-20140225/#quad-semantics"
 * >RDF-1.1 Concepts and Abstract Syntax</a>, a W3C Working Group Note published
 * on 25 February 2014.
 * <p>
 * A <code>Quad</code> object in Commons RDF is considered
 * <strong>immutable</strong>, that is, over its life time it will have
 * consistent behavior for its {@link #equals(Object)}, and the instances
 * returned from {@link #getGraphName()}, {@link #getSubject()},
 * {@link #getPredicate()}, {@link #getObject()} and {@link #asTriple()} will
 * have consistent {@link Object#equals(Object)} behavior.
 * <p>
 * Note that <code>Quad</code> methods are not required to return object
 * identical (<code>==</code>) instances as long as they are equivalent
 * according to {@link Object#equals(Object)}. Specialisations of
 * <code>Quad</code> may provide additional methods that are documented to be
 * mutable.
 * <p>
 * <code>Quad</code> methods are <strong>thread-safe</strong>, however
 * specialisations may provide additional methods that are documented to not be
 * thread-safe.
 * <p>
 * <code>Quad</code>s can be safely used in hashing collections like
 * {@link java.util.HashSet} and {@link java.util.HashMap}.
 * <p>
 * Any <code>Quad</code> can be used interchangeably across Commons RDF
 * implementations.
 *
 * @since 0.3.0-incubating
 * @see Dataset
 * @see RDF#createQuad(BlankNodeOrIRI,BlankNodeOrIRI,IRI,RDFTerm)
 * @see <a href="http://www.w3.org/TR/2014/NOTE-rdf11-datasets-20140225/">RDF
 *      1.1: On Semantics of RDF Datasets</a>
 * @see <a href="http://www.w3.org/TR/rdf11-concepts/#section-dataset"> </a>
 */
public interface Quad extends QuadLike<BlankNodeOrIRI> {

    /**
     * The graph name (graph label) of this quad, if present.
     *
     * If {@link Optional#isPresent()}, then the {@link Optional#get()} is
     * either a {@link BlankNode} or an {@link IRI}, indicating the
     * <a href="https://www.w3.org/TR/rdf11-concepts/#dfn-named-graph">graph
     * name</a> of this Quad. If the graph name is not present, e.g. the value
     * is {@link Optional#empty()}, it indicates that this Quad is in the
     * <a href="https://www.w3.org/TR/rdf11-concepts/#dfn-default-graph">default
     * graph</a>.
     *
     * @return If {@link Optional#isPresent()}, the graph name
     *         {@link BlankNodeOrIRI} of this quad, otherwise
     *         {@link Optional#empty()}, indicating the default graph.
     *
     * @see <a href="https://www.w3.org/TR/rdf11-concepts/#dfn-rdf-dataset">RDF-
     *      1.1 Dataset</a>
     */
    @Override
    Optional<BlankNodeOrIRI> getGraphName();

    /**
     * The subject of this quad, which may be either a {@link BlankNode} or an
     * {@link IRI}, which are represented in Commons RDF by the interface
     * {@link BlankNodeOrIRI}.
     *
     * @return The subject {@link BlankNodeOrIRI} of this quad.
     * @see <a href="http://www.w3.org/TR/rdf11-concepts/#dfn-subject">RDF-1.1
     *      Triple subject</a>
     */
    @Override
    BlankNodeOrIRI getSubject();

    /**
     * The predicate {@link IRI} of this quad.
     *
     * @return The predicate {@link IRI} of this quad.
     * @see <a href="http://www.w3.org/TR/rdf11-concepts/#dfn-predicate">RDF-1.1
     *      Triple predicate</a>
     */
    @Override
    IRI getPredicate();

    /**
     * The object of this quad, which may be either a {@link BlankNode}, an
     * {@link IRI}, or a {@link Literal}, which are represented in Commons RDF
     * by the interface {@link RDFTerm}.
     *
     * @return The object {@link RDFTerm} of this quad.
     * @see <a href="http://www.w3.org/TR/rdf11-concepts/#dfn-object">RDF-1.1
     *      Triple object</a>
     */
    @Override
    RDFTerm getObject();

    /**
     * Adapt this Quad to a Triple.
     * <p>
     * The returned {@link Triple} will have equivalent values returned from the
     * methods {@link TripleLike#getSubject()},
     * {@link TripleLike#getPredicate()} and {@link TripleLike#getObject()}.
     * <p>
     * The returned {@link Triple} MUST NOT be {@link #equals(Object)} to this
     * {@link Quad}, even if this quad has a default graph
     * {@link #getGraphName()} value of {@link Optional#empty()}, but MUST
     * follow the {@link Triple#equals(Object)} semantics. This means that the
     * following MUST be true:
     *
     * <pre>
     * Quad q1, q2;
     * if (q1.equals(q2)) {
     *     assert (q1.asTriple().equals(q2.asTriple()));
     * } else if (q1.asTriple().equals(q2.asTriple())) {
     *     assert (q1.getSubject().equals(q2.getSubject()));
     *     assert (q1.getPredicate().equals(q2.getPredicate()));
     *     assert (q1.getObject().equals(q2.getObject()));
     *     assert (!q1.getGraphName().equals(q2.getGraphName()));
     * }
     * </pre>
     *
     * The <code>default</code> implementation of this method return a proxy
     * {@link Triple} instance that keeps a reference to this {@link Quad} to
     * call the underlying {@link TripleLike} methods, but supplies a
     * {@link Triple} compatible implementation of {@link Triple#equals(Object)}
     * and {@link Triple#hashCode()}. Implementations may override this method,
     * e.g. for a more efficient solution.
     *
     * @return A {@link Triple} that contains the same {@link TripleLike}
     *         properties as this Quad.
     */
    default Triple asTriple() {
        return new Triple() {
            @Override
            public BlankNodeOrIRI getSubject() {
                return Quad.this.getSubject();
            }

            @Override
            public IRI getPredicate() {
                return Quad.this.getPredicate();
            }

            @Override
            public RDFTerm getObject() {
                return Quad.this.getObject();
            }

            @Override
            public boolean equals(final Object obj) {
                if (obj == this) {
                    return true;
                }
                if (!(obj instanceof Triple)) {
                    return false;
                }
                final Triple other = (Triple) obj;
                return Objects.equals(getSubject(), other.getSubject())
                        && Objects.equals(getPredicate(), other.getPredicate())
                        && Objects.equals(getObject(), other.getObject());
            }

            @Override
            public int hashCode() {
                return Objects.hash(getSubject(), getPredicate(), getObject());
            }
        };
    }

    /**
     * Check it this Quad is equal to another Quad.
     * <p>
     * Two Quads are equal if and only if their {@link #getGraphName()},
     * {@link #getSubject()}, {@link #getPredicate()} and {@link #getObject()}
     * are equal.
     * </p>
     * <p>
     * Implementations MUST also override {@link #hashCode()} so that two equal
     * Quads produce the same hash code.
     * </p>
     * <p>
     * Note that a {@link Quad} MUST NOT be equal to a {@link Triple}, even if
     * this Quad's {@link #getGraphName()} is {@link Optional#empty()}. To test
     * triple-like equivalence, callers can use:
     * </p>
     *
     * <pre>
     * Quad q1;
     * Triple t2;
     * q1.asTriple().equals(t2));
     * </pre>
     *
     * @param other
     *            Another object
     * @return true if other is a Quad and is equal to this
     * @see Object#equals(Object)
     */
    @Override
    boolean equals(Object other);

    /**
     * Calculate a hash code for this Quad.
     * <p>
     * The returned hash code MUST be equal to the result of
     * {@link Objects#hash(Object...)} with the arguments {@link #getSubject()},
     * {@link #getPredicate()}, {@link #getObject()}, {@link #getGraphName()}.
     * <p>
     * This method MUST be implemented in conjunction with
     * {@link #equals(Object)} so that two equal {@link Quad}s produce the same
     * hash code.
     *
     * @return a hash code value for this Quad.
     * @see Object#hashCode()
     * @see Objects#hash(Object...)
     */
    @Override
    int hashCode();

}
