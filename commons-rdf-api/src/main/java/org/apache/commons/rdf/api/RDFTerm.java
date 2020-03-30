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
 * An <a href= "http://www.w3.org/TR/rdf11-concepts/#dfn-rdf-term" >RDF-1.1
 * Term</a>, as defined by
 * <a href= "http://www.w3.org/TR/rdf11-concepts/" >RDF-1.1 Concepts and
 * Abstract Syntax</a>, a W3C Recommendation published on 25 February 2014.
 * <p>
 * A {@link RDFTerm} object in Commons RDF is considered
 * <strong>immutable</strong>, that is, over its life time it will have
 * consistent behavior for its {@link #equals(Object)} and {@link #hashCode()},
 * and objects returned from its getter methods (e.g. {@link IRI#getIRIString()}
 * and {@link Literal#getLanguageTag()}) will have consistent
 * {@link #equals(Object)} behavior.
 * <p>
 * Note that methods in <code>RDFTerm</code> and its Commons RDF specialisations
 * {@link IRI}, {@link BlankNode} and {@link Literal} are not required to return
 * object identical (<code>==</code>) instances as long as they are equivalent
 * according to their {@link Object#equals(Object)}. Further specialisations may
 * provide additional methods that are documented to be mutable.
 * <p>
 * Methods in <code>RDFTerm</code> and its Commons RDF specialisations
 * {@link IRI}, {@link BlankNode} and {@link Literal} are
 * <strong>thread-safe</strong>, however further specialisations may add
 * additional methods that are documented to not be thread-safe.
 * <p>
 * <code>RDFTerm</code>s can be safely used in hashing collections like
 * {@link java.util.HashSet} and {@link java.util.HashMap}.
 * <p>
 * Any <code>RDFTerm</code> can be used interchangeably across Commons RDF
 * implementations.
 *
 * @see <a href= "http://www.w3.org/TR/rdf11-concepts/#dfn-rdf-term" >RDF-1.1
 *      Term</a>
 */
public interface RDFTerm {

    /**
     * Return the term serialised as specified by the RDF-1.1 N-Triples
     * Canonical form.
     *
     * @return The term serialised as RDF-1.1 N-Triples.
     * @see <a href="http://www.w3.org/TR/n-triples/#canonical-ntriples">
     *      RDF-1.1 N-Triples Canonical form</a>
     */
    String ntriplesString();

	/**
	 * Check it this RDFTerm is equal to another RDFTerm.
	 * <p>
	 * If this object is an {@link IRI}, equality is checked using
	 * {@link IRI#equals(Object)}, or if this object is a {@link BlankNode},
	 * equality is checked using {@link BlankNode#equals(Object)}, or if this
	 * object is a {@link Literal}, equality is checked using
	 * {@link Literal#equals(Object)}.
	 * <p>
	 * Implementations MUST also override {@link #hashCode()} so that two equal
	 * Literals produce the same hash code.
	 *
	 * @see IRI#equals(Object)
	 * @see BlankNode#equals(Object)
	 * @see Literal#equals(Object)
	 *
	 * @param other
	 *            Another object
	 * @return true if other is a RDFTerm and is equal to this
	 */
    @Override
    boolean equals(Object other);

	/**
	 * Calculate a hash code for this RDFTerm.
	 * <p>
	 * As an {@link RDFTerm} is <em>immutable</em>, this method will always
	 * return the same hashCode over the lifetime of this object.
	 * <p>
	 * This method MUST be implemented in conjunction with
	 * {@link #equals(Object)} so that two equal RDFTerm produce the same hash
	 * code.
	 *
	 * @see IRI#hashCode()
	 * @see Literal#hashCode()
	 * @see BlankNode#hashCode()
	 *
	 * @return a hash code value for this RDFTerm.
	 */
    @Override
    int hashCode();

}
