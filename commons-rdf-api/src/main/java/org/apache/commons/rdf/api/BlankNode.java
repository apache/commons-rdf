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

import java.util.UUID;

/**
 * A <a href= "http://www.w3.org/TR/rdf11-concepts/#dfn-blank-node" >RDF-1.1
 * Blank Node</a>, as defined by
 * <a href= "http://www.w3.org/TR/rdf11-concepts/#section-blank-nodes" >RDF-1.1
 * Concepts and Abstract Syntax</a>, a W3C Recommendation published on 25
 * February 2014.<br>
 *
 * Note: <blockquote>
 * <a href="http://www.w3.org/TR/rdf11-concepts/#dfn-blank-node">Blank nodes</a>
 * are disjoint from IRIs and literals. Otherwise, the set of possible blank
 * nodes is arbitrary. RDF makes no reference to any internal structure of blank
 * nodes. </blockquote>
 *
 * Also note that: <blockquote> <a href=
 * "http://www.w3.org/TR/rdf11-concepts/#dfn-blank-node-identifier">Blank node
 * identifiers</a> are local identifiers that are used in some concrete RDF
 * syntaxes or RDF store implementations. They are always <em>locally
 * scoped</em> to the file or RDF store, and are <em>not</em> persistent or
 * portable identifiers for blank nodes. Blank node identifiers are <em>not</em>
 * part of the RDF abstract syntax, but are entirely dependent on the concrete
 * syntax or implementation. The syntactic restrictions on blank node
 * identifiers, if any, therefore also depend on the concrete RDF syntax or
 * implementation.
 *
 * Implementations that handle blank node identifiers in concrete syntaxes need
 * to be careful not to create the same blank node from multiple occurrences of
 * the same blank node identifier except in situations where this is supported
 * by the syntax. </blockquote>
 *
 * A BlankNode SHOULD contain a {@link UUID}-derived string as part of its
 * universally unique {@link #uniqueReference()}.
 *
 * @see RDF#createBlankNode()
 * @see RDF#createBlankNode(String)
 * @see <a href="http://www.w3.org/TR/rdf11-concepts/#dfn-blank-node">RDF-1.1
 *      Blank Node</a>
 */
public interface BlankNode extends BlankNodeOrIRI {

    /**
     * Return a reference for uniquely identifying the blank node.
     * <p>
     * The reference string MUST universally and uniquely identify this blank
     * node. That is, different blank nodes created separately in different JVMs
     * or from different {@link RDF} instances MUST NOT have the same reference
     * string.
     * <p>
     * The {@link #uniqueReference()} of two <code>BlankNode</code> instances
     * MUST be equal if and only if the two blank nodes are equal according to
     * {@link #equals(Object)}.
     * <p>
     * Clients should not assume any particular structure of the reference
     * string, however it is recommended that the reference string contain a
     * UUID-derived string, e.g. as returned from {@link UUID#toString()}.
     * <p>
     * <strong>IMPORTANT:</strong> This is not a
     * <a href="http://www.w3.org/TR/rdf11-concepts/#dfn-blank-node-identifier">
     * blank node identifier</a> nor a serialization/syntax label, and there are
     * no guarantees that it is a valid identifier in any concrete RDF syntax.
     * For an N-Triples compatible identifier, use {@link #ntriplesString()}.
     *
     * @return A universally unique reference to identify this {@link BlankNode}
     */
    String uniqueReference();

    /**
     * Check it this BlankNode is equal to another BlankNode. Two BlankNodes
     * MUST be equal if, and only if, they have the same
     * {@link #uniqueReference()}.
     * <p>
     * Implementations MUST also override {@link #hashCode()} so that two equal
     * Literals produce the same hash code.
     *
     * @param other
     *            Another object
     * @return true if other is a BlankNode instance that represent the same
     *         blank node
     * @see Object#equals(Object)
     */
    @Override
    boolean equals(Object other);

    /**
     * Calculate a hash code for this BlankNode.
     * <p>
     * The returned hash code MUST be equal to the {@link String#hashCode()} of
     * the {@link #uniqueReference()}.
     * <p>
     * This method MUST be implemented in conjunction with
     * {@link #equals(Object)} so that two equal BlankNodes produce the same
     * hash code.
     *
     * @return a hash code value for this BlankNode.
     * @see Object#hashCode()
     */
    @Override
    int hashCode();

}
