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
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
 * A <a href= "http://www.w3.org/TR/rdf11-concepts/#dfn-blank-node" >RDF-1.1
 * Blank Node</a>, as defined by <a href=
 * "http://www.w3.org/TR/rdf11-concepts/#section-blank-nodes" >RDF-1.1 Concepts
 * and Abstract Syntax</a>, a W3C Recommendation published on 25 February 2014.<br>
 * <p>
 * Note: 
 * <blockquote>
 *   <a href="http://www.w3.org/TR/rdf11-concepts/#dfn-blank-node">Blank nodes</a>
 *   are disjoint from IRIs and literals. Otherwise, the
 *   set of possible blank nodes is arbitrary. RDF makes no reference to any
 *   internal structure of blank nodes.
 * </blockquote>
 * <p>
 * Also note that: 
 * <blockquote>
 * <a href="http://www.w3.org/TR/rdf11-concepts/#dfn-blank-node-identifier">Blank node identifiers</a>
 * are local identifiers that are used in
 * some concrete RDF syntaxes or RDF store implementations. They are always
 * <em>locally scoped</em> to the file or RDF store, and are <em>not</em> persistent 
 * or portable
 * identifiers for blank nodes. Blank node identifiers are <em>not</em> 
 * part of the RDF
 * abstract syntax, but are entirely dependent on the concrete syntax or
 * implementation.
 * The syntactic restrictions on blank node identifiers, if any,
 * therefore also depend on the concrete RDF syntax or implementation.
 * <p>
 * Implementations that handle blank node identifiers in concrete syntaxes need
 * to be careful not to create the same blank node from multiple occurrences of
 * the same blank node identifier except in situations where this is supported
 * by the syntax.
 * </blockquote>
 *
 * @see <a href="http://www.w3.org/TR/rdf11-concepts/#dfn-blank-node">RDF-1.1
 *      Blank Node</a>
 */
public interface BlankNode extends BlankNodeOrIRI {

	/**
	 * Return a <a href=
	 * "http://www.w3.org/TR/rdf11-concepts/#dfn-blank-node-identifier"
	 * >label</a> for the blank node. This is not a serialization/syntax label.
	 * It should be uniquely identifying within the local scope it is created in
	 * but has no uniqueness guarantees other than that.
	 * <p>
	 * In particular, the existence of two objects of type {@link BlankNode}
	 * with the same value returned from {@link #internalIdentifier()} are not
	 * equivalent unless they are known to have been created in the same local
	 * scope (see {@link #equals(Object)})
	 * <p>
	 * An example of a local scope may be an instance of a Java Virtual Machine
	 * (JVM). In the context of a JVM instance, an implementor may support
	 * insertion and removal of {@link Triple} objects containing Blank Nodes
	 * without modifying the blank node labels.
	 * <p>
	 * Another example of a local scope may be a <a
	 * href="http://www.w3.org/TR/rdf11-concepts/#section-rdf-graph">Graph</a>
	 * or <a
	 * href="http://www.w3.org/TR/rdf11-concepts/#section-dataset">Dataset</a>
	 * created from a single document. In this context, an implementor should
	 * reasonably guarantee that the label returned by getLabel only maps to
	 * equivalent blank nodes in the same Graph or Dataset, but they may not
	 * guarantee that it is unique for the JVM instance. In this case, the
	 * implementor may support a mechanism to provide a mapping for blank nodes
	 * between Graph or Dataset instances to guarantee their uniqueness.
	 * <p>
	 * If implementors support <a
	 * href="http://www.w3.org/TR/rdf11-concepts/#section-skolemization"
	 * >Skolemisation</a>, they may map instances of {@link BlankNode} objects
	 * to {@link IRI} objects to reduce scoping issues.
	 * <p>
	 * It is not a requirement for the internal identifier to be a part of the
	 * {@link #ntriplesString()}, except that two BlankNode instances with the
	 * same internalIdentifier() and same local scope should have the same
	 * {@link #ntriplesString()}.
	 *
	 * @return An internal, system identifier for the {@link BlankNode}.
	 */
	String internalIdentifier();

	/**
	 * Check it this BlankNode is equal to another BlankNode. <blockquote> <a
	 * href
	 * ="http://www.w3.org/TR/rdf11-concepts/#dfn-blank-node-identifier">Blank
	 * node identifiers</a> are local identifiers that are used in some concrete
	 * RDF syntaxes or RDF store implementations. They are always locally scoped
	 * to the file or RDF store, and are <em>not</em> persistent or portable
	 * identifiers for blank nodes. Blank node identifiers are <em>not</em> part
	 * of the RDF abstract syntax, but are entirely dependent on the concrete
	 * syntax or implementation. The syntactic restrictions on blank node
	 * identifiers, if any, therefore also depend on the concrete RDF syntax or
	 * implementation. 
	 * <p>Implementations that handle blank node identifiers in
	 * concrete syntaxes need to be careful not to create the same blank node
	 * from multiple occurrences of the same blank node identifier except in
	 * situations where this is supported by the syntax. 
	 * </blockquote>
	 * <p>
	 * Implementations MUST check the local scope, as two BlankNode in different
	 * Graphs MUST differ. On the other hand, two BlankNodes found in triples of
	 * the same Graph instance MUST equal if and only if they have the same
	 * {@link #internalIdentifier()}.
	 * </p>
	 * <p>
	 * Implementations MUST also override {@link #hashCode()} so that two equal
	 * Literals produce the same hash code.
	 * </p>
	 * 
	 * @see Object#equals(Object)
	 * 
	 * @param other
	 *            Another object
	 * @return true if other is a BlankNode, is in the same local scope and is
	 *         equal to this BlankNode
	 */
	@Override
	public boolean equals(Object other);

	/**
	 * Calculate a hash code for this BlankNode.
	 * <p>
	 * This method MUST be implemented when implementing {@link #equals(Object)}
	 * so that two equal BlankNodes produce the same hash code.
	 * 
	 * @see Object#hashCode()
	 * 
	 * @return a hash code value for this BlankNode.
	 */
	@Override
	public int hashCode();

}
