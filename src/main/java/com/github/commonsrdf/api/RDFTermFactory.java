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
package com.github.commonsrdf.api;

import java.util.Locale;

/**
 * Factory for creating RDFTerm and Graph instances.
 * <p>
 * It is not specified how an implementation should provide a RDFTermFactory.
 * <p>
 * If an implementation does not support a particular method (e.g. it requires
 * additional parameters or can't create graphs), then it MAY throw
 * UnsupportedOperationException, as provided by the default implementations
 * here.
 * <p>
 * If a factory method does not allow or support a provided parameter, e.g.
 * because an IRI is considered invalid, then it SHOULD throw
 * IllegalArgumentException.
 * 
 * 
 * @see RDFTerm
 * @see Graph
 * 
 */
public interface RDFTermFactory {

	/**
	 * Create a new blank node.
	 * <p>
	 * Two BlankNodes created with this method MUST NOT be equal.
	 * <p>
	 * If supported, the {@link BlankNode#internalIdentifier()} of the returned
	 * blank node MUST be an auto-generated value.
	 * 
	 * @return A new BlankNode
	 * @throws UnsupportedOperationException
	 */
	default BlankNode createBlankNode() throws UnsupportedOperationException {
		throw new UnsupportedOperationException(
				"createBlankNode() not supported");
	}

	/**
	 * Create a blank node for the given internal identifier.
	 * <p>
	 * Two BlankNodes created with the same identifier using this method MUST be
	 * equal if they are in the same local scope (e.g. in the same Graph). See
	 * {@link BlankNode#equals(Object)}.
	 * <p>
	 * If supported, the {@link BlankNode#internalIdentifier()} of the returned
	 * blank node SHOULD be equal to the provided identifier.
	 * 
	 * @param identifier
	 *            An internal identifier for the blank node.
	 * @return A BlankNode for the given identifier
	 * @throws IllegalArgumentException
	 *             if the identifier is not acceptable, e.g. was empty or
	 *             contained unsupported characters.
	 * @throws UnsupportedOperationException
	 *             if createBlankNode(String) is not implemented or supported.
	 */
	default BlankNode createBlankNode(String identifier)
			throws IllegalArgumentException, UnsupportedOperationException {
		throw new UnsupportedOperationException(
				"createBlankNode(String) not supported");
	}

	/**
	 * Create a new graph.
	 * <p>
	 * It is undefined if the graph will be persisted by any underlying storage
	 * mechanism.
	 * 
	 * @return A new Graph
	 * @throws UnsupportedOperationException
	 *             if createGraph() is not implemented or supported
	 */
	default Graph createGraph() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("createGraph() not supported");
	}

	/**
	 * Create an IRI from a (possibly escaped) String.
	 * <p>
	 * The provided iri string MUST be valid according to the <a
	 * href="http://www.w3.org/TR/rdf11-concepts/#dfn-iri">W3C RDF-1.1 IRI</a>
	 * definition.
	 * 
	 * @param iri
	 *            Internationalized Resource Identifier
	 * @return A new IRI
	 * @throws IllegalArgumentException
	 *             If the provided string is not acceptable, e.g. does not
	 *             conform to the RFC3987 syntax.
	 * @throws UnsupportedOperationException
	 *             If the createIRI(String) method is not implemented or
	 *             supported. If the method is supported, but not for the
	 *             particular iri string provided (e.g. if only absolute ASCII
	 *             URIs are supported by the implementation), then an
	 *             IllegalArgumentException should be thrown.
	 */
	default IRI createIRI(String iri) throws IllegalArgumentException,
			UnsupportedOperationException {
		throw new UnsupportedOperationException(
				"createIRI(String) not supported");
	}

	/**
	 * Create a simple literal.
	 * <p>
	 * The provided lexical form should not be escaped in any sense, e.g. should
	 * not include "quotes" unless those are part of the literal value.
	 * <p>
	 * The returned Literal MUST have a {@link Literal#getLexicalForm()} that is
	 * equal to the provided lexical form, MUST NOT have a
	 * {@link Literal#getLanguageTag()} present, and SHOULD return a
	 * {@link Literal#getDatatype()} that is equal to the IRI
	 * <code>http://www.w3.org/2001/XMLSchema#string</code>.
	 * 
	 * 
	 * @param lexicalForm
	 *            The literal value in plain text
	 * @return The created Literal
	 * @throws IllegalArgumentException
	 *             If the provided lexicalForm is not acceptable, e.g. because
	 *             it is too large for an underlying storage.
	 * @throws UnsupportedOperationException
	 *             If the createLiteral(String) method is not implemented or
	 *             supported.
	 */
	default Literal createLiteral(String lexicalForm)
			throws IllegalArgumentException, UnsupportedOperationException {
		throw new UnsupportedOperationException(
				"createLiteral(String) not supported");
	}

	/**
	 * Create a literal with the specified data type.
	 * <p>
	 * The provided lexical form should not be escaped in any sense, e.g. should
	 * not include "quotes" unless those are part of the literal value.
	 * <p>
	 * It is RECOMMENDED that the provided dataType is one of the <a
	 * href="http://www.w3.org/TR/rdf11-concepts/#xsd-datatypes">RDF-compatible
	 * XSD types</a>.
	 * <p>
	 * The provided lexical form SHOULD be in the <a
	 * href="http://www.w3.org/TR/rdf11-concepts/#dfn-lexical-space">lexical
	 * space</a> of the provided dataType.
	 * <p>
	 * The returned Literal SHOULD have a {@link Literal#getLexicalForm()} that
	 * is equal to the provided lexicalForm, MUST NOT have a
	 * {@link Literal#getLanguageTag()} present, and SHOULD return a
	 * {@link Literal#getDatatype()} that is equal to the provided dataType IRI.
	 * 
	 * @param lexicalForm
	 *            The literal value
	 * @param dataType
	 *            The data type IRI for the literal value, e.g.
	 *            <code>http://www.w3.org/2001/XMLSchema#integer</code>
	 * @return The created Literal
	 * @throws IllegalArgumentException
	 *             If any of the provided arguments are not acceptable, e.g.
	 *             because the provided dataType is not permitted.
	 * @throws UnsupportedOperationException
	 *             If the createLiteral(String,IRI) method is not implemented or
	 *             supported.
	 */
	default Literal createLiteral(String lexicalForm, IRI dataType)
			throws IllegalArgumentException, UnsupportedOperationException {
		throw new UnsupportedOperationException(
				"createLiteral(String) not supported");
	}

	/**
	 * Create a language-tagged literal.
	 * <p>
	 * The provided lexical form should not be escaped in any sense, e.g. should
	 * not include "quotes" unless those are part of the literal value.
	 * <p>
	 * The provided language tag MUST be valid according to <a
	 * href"http://tools.ietf.org/html/bcp47">BCP47</a>, e.g. <code>en</code>.
	 * <p>
	 * The provided language tag <a
	 * href="http://www.w3.org/TR/rdf11-concepts/#dfn-language-tagged-string"
	 * >MAY be converted to lower case</a>.
	 * <p>
	 * The returned Literal SHOULD have a {@link Literal#getLexicalForm()} which
	 * is equal to the provided lexicalForm, MUST return a
	 * {@link Literal#getDatatype()} that is equal to the IRI
	 * <code>http://www.w3.org/1999/02/22-rdf-syntax-ns#langString</code>, and
	 * MUST have a {@link Literal#getLanguageTag()} present which SHOULD be
	 * equal to the provided language tag (compared as
	 * {@link String#toLowerCase(Locale)} in {@link Locale#ENGLISH}).
	 * 
	 * @param lexicalForm
	 *            The literal value
	 * @param languageTag
	 *            The non-empty language tag as defined by <a
	 *            href"http://tools.ietf.org/html/bcp47">BCP47</a>
	 * @return The created Literal
	 * @throws IllegalArgumentException
	 *             If the provided values are not acceptable, e.g. because the
	 *             languageTag was syntactically invalid.
	 * @throws UnsupportedOperationException
	 *             If the createLiteral(String,String) method is not implemented
	 *             or supported.
	 */
	default Literal createLiteral(String lexicalForm, String languageTag)
			throws IllegalArgumentException, UnsupportedOperationException {
		throw new UnsupportedOperationException(
				"createLiteral(String,String) not supported");
	}

	/**
	 * Create a triple.
	 * <p>
	 * The returned Triple SHOULD have a {@link Triple#getSubject()} that is
	 * equal to the provided subject, a {@link Triple#getPredicate()} that is
	 * equal to the provided predicate, and a {@link Triple#getObject()} that is
	 * equal to the provided object.
	 * 
	 * @param subject
	 *            The IRI or BlankNode that is the subject of the triple
	 * @param predicate
	 *            The IRI that is the predicate of the triple
	 * @param object
	 *            The IRI, BlankNode or Literal that is the object of the triple
	 * @return The created Triple
	 * @throws IllegalArgumentException
	 *             If any of the provided arguments are not acceptable, e.g.
	 *             because a Literal has a lexicalForm that is too large for an
	 *             underlying storage.
	 * @throws UnsupportedOperationException
	 *             if createTriple is not supported
	 */
	default Triple createTriple(BlankNodeOrIRI subject, IRI predicate,
			RDFTerm object) throws IllegalArgumentException,
			UnsupportedOperationException {
		throw new UnsupportedOperationException(
				"createTriple(BlankNodeOrIRI,IRI,RDFTerm) not supported");
	}

}
