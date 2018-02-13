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
 * An <a href= "http://www.w3.org/TR/rdf11-concepts/#dfn-iri" >RDF-1.1 IRI</a>,
 * as defined by <a href= "http://www.w3.org/TR/rdf11-concepts/#section-IRIs" >RDF-1.1
 * Concepts and Abstract Syntax</a>, a W3C Recommendation published on 25
 * February 2014.
 *
 * @see RDF#createIRI(String)
 */
public interface IRI extends BlankNodeOrIRI {

    /**
     * Return the IRI encoded as a native Unicode String.<br>
     *
     * The returned string must not include URL-encoding to escape non-ASCII
     * characters.
     *
     * @return The IRI encoded as a native Unicode String.
     */
    String getIRIString();

    /**
     * Check it this IRI is equal to another IRI. <blockquote>
     * <a href="http://www.w3.org/TR/rdf11-concepts/#section-IRIs">IRI
     * equality</a>: Two IRIs are equal if and only if they are equivalent under
     * Simple String Comparison according to section 5.1 of [RFC3987]. Further
     * normalization MUST NOT be performed when comparing IRIs for equality.
     * </blockquote>
     *
     * Two IRI instances are equal if and only if their {@link #getIRIString()}
     * are equal.
     *
     * Implementations MUST also override {@link #hashCode()} so that two equal
     * IRIs produce the same hash code.
     *
     * @param other
     *            Another object
     * @return true if other is an IRI and is equal to this
     * @see Object#equals(Object)
     */
    @Override
    boolean equals(Object other);

    /**
     * Calculate a hash code for this IRI.
     * <p>
     * The returned hash code MUST be equal to the {@link String#hashCode()} of
     * the {@link #getIRIString()}.
     * <p>
     * This method MUST be implemented in conjunction with
     * {@link #equals(Object)} so that two equal IRIs produce the same hash
     * code.
     *
     * @return a hash code value for this IRI.
     * @see Object#hashCode()
     */
    @Override
    int hashCode();
}
