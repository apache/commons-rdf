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

import java.io.Serializable;
import java.util.Locale;

/**
 * Factory for creating RDFTerm and Graph instances.
 * <p>
 * It is not specified how an implementation should provide a RDFTermFactory.
 * <p>
 * If an implementation does not support a particular method (e.g. it requires
 * additional parameters or can't create graphs), then it MAY throw
 * {@link UnsupportedOperationException}, as provided by the <code>default</code>
 * implementations in this interface.
 * <p>
 * If a factory method does not allow or support a provided parameter, e.g.
 * because an IRI is considered invalid, then it SHOULD throw
 * {@link IllegalArgumentException}.
 *
 * @see RDFTerm
 * @see Graph
 */
public interface RDFTermFactory {

    /**
     * Create a new blank node.
     * <p>
     * The returned blank node MUST NOT be equal to any existing
     * {@link BlankNode} instances according to {@link BlankNode#equals(Object)}.
     *
     * @return A new, unique {@link BlankNode}
     * @throws UnsupportedOperationException
     *             If the operation is not supported.
     */
    default BlankNode createBlankNode() throws UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "createBlankNode() not supported");
    }

    /**
     * Create a blank node based on the given name.
     * <p>
     * All {@link BlankNode}s created with the given <code>name</code>
     * <em>on a particular instance</em> of <code>RDFTermFactory</code> MUST be
     * equivalent according to {@link BlankNode#equals(Object)},
     * <p>
     * The returned BlankNode MUST NOT be equal to <code>BlankNode</code>
     * instances returned for any other <code>name</code> or those returned from
     * {@link #createBlankNode()}.
     * <p>
     * The returned BlankNode SHOULD NOT be equivalent to any BlankNodes created
     * on a <em>different</em> <code>RDFTermFactory</code> instance, e.g.
     * different instances of <code>RDFTermFactory</code> should produce
     * different blank nodes for the same <code>name</code> unless they
     * purposely are intending to create equivalent {@link BlankNode}
     * instances (e.g. a reinstated {@link Serializable} factory).
     *
     * @param name
     *            A non-empty, non-null, String that is unique to this blank
     *            node in the context of this {@link RDFTermFactory}.
     * @return A BlankNode for the given name
     * @throws UnsupportedOperationException
     *             If the operation is not supported.
     */
    default BlankNode createBlankNode(String name)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "createBlankNode(String) not supported");
    }

    /**
     * Create a new graph.
     *
     * It is undefined if the graph will be persisted by any underlying storage
     * mechanism.
     *
     * @return A new Graph
     * @throws UnsupportedOperationException If the operation is not supported.
     */
    default Graph createGraph() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("createGraph() not supported");
    }

    /**
     * Create a new dataset.
     *
     * It is undefined if the dataset will be persisted by any underlying storage
     * mechanism.
     *
     * @return A new Dataset
     * @throws UnsupportedOperationException If the operation is not supported.
     */
    default Dataset createDataset() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("createDataset() not supported");
    }

    /**
     * Create an IRI from a (possibly escaped) String.
     *
     * The provided iri string MUST be valid according to the <a
     * href="http://www.w3.org/TR/rdf11-concepts/#dfn-iri">W3C RDF-1.1 IRI</a>
     * definition.
     *
     * @param iri Internationalized Resource Identifier
     * @return A new IRI
     * @throws IllegalArgumentException      If the provided string is not acceptable, e.g. does not
     *                                       conform to the RFC3987 syntax.
     * @throws UnsupportedOperationException If the operation is not supported.
     */
    default IRI createIRI(String iri) throws IllegalArgumentException,
            UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "createIRI(String) not supported");
    }

    /**
     * Create a simple literal.
     *
     * The provided lexical form should not be escaped in any sense, e.g. should
     * not include "quotes" unless those are part of the literal value.
     *
     * The returned Literal MUST have a {@link Literal#getLexicalForm()} that is
     * equal to the provided lexical form, MUST NOT have a
     * {@link Literal#getLanguageTag()} present, and SHOULD return a
     * {@link Literal#getDatatype()} that is equal to the IRI
     * <code>http://www.w3.org/2001/XMLSchema#string</code>.
     *
     * @param lexicalForm The literal value in plain text
     * @return The created Literal
     * @throws IllegalArgumentException      If the provided lexicalForm is not acceptable, e.g. because
     *                                       it is too large for an underlying storage.
     * @throws UnsupportedOperationException If the operation is not supported.
     */
    default Literal createLiteral(String lexicalForm)
            throws IllegalArgumentException, UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "createLiteral(String) not supported");
    }

    /**
     * Create a literal with the specified data type.
     *
     * The provided lexical form should not be escaped in any sense, e.g. should
     * not include "quotes" unless those are part of the literal value.
     *
     * It is RECOMMENDED that the provided dataType is one of the <a
     * href="http://www.w3.org/TR/rdf11-concepts/#xsd-datatypes">RDF-compatible
     * XSD types</a>.
     *
     * The provided lexical form SHOULD be in the <a
     * href="http://www.w3.org/TR/rdf11-concepts/#dfn-lexical-space">lexical
     * space</a> of the provided dataType.
     *
     * The returned Literal SHOULD have a {@link Literal#getLexicalForm()} that
     * is equal to the provided lexicalForm, MUST NOT have a
     * {@link Literal#getLanguageTag()} present, and MUST return a
     * {@link Literal#getDatatype()} that is equivalent to the provided dataType
     * IRI.
     *
     * @param lexicalForm The literal value
     * @param dataType    The data type IRI for the literal value, e.g.
     *                    <code>http://www.w3.org/2001/XMLSchema#integer</code>
     * @return The created Literal
     * @throws IllegalArgumentException      If any of the provided arguments are not acceptable, e.g.
     *                                       because the provided dataType is not permitted.
     * @throws UnsupportedOperationException If the operation is not supported.
     */
    default Literal createLiteral(String lexicalForm, IRI dataType)
            throws IllegalArgumentException, UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "createLiteral(String) not supported");
    }

    /**
     * Create a language-tagged literal.
     *
     * The provided lexical form should not be escaped in any sense, e.g. should
     * not include "quotes" unless those are part of the literal value.
     *
     * The provided language tag MUST be valid according to <a
     * href="http://tools.ietf.org/html/bcp47">BCP47</a>, e.g. <code>en</code>.
     *
     * The provided language tag <a
     * href="http://www.w3.org/TR/rdf11-concepts/#dfn-language-tagged-string"
     * >MAY be converted to lower case</a>.
     *
     * The returned Literal SHOULD have a {@link Literal#getLexicalForm()} which
     * is equal to the provided lexicalForm, MUST return a
     * {@link Literal#getDatatype()} that is equal to the IRI
     * <code>http://www.w3.org/1999/02/22-rdf-syntax-ns#langString</code>, and
     * MUST have a {@link Literal#getLanguageTag()} present which SHOULD be
     * equal to the provided language tag (compared as
     * {@link String#toLowerCase(Locale)} using {@link Locale#ENGLISH}).
     *
     * @param lexicalForm The literal value
     * @param languageTag The non-empty language tag as defined by <a
     *                    href="http://tools.ietf.org/html/bcp47">BCP47</a>
     * @return The created Literal
     * @throws IllegalArgumentException      If the provided values are not acceptable, e.g. because the
     *                                       languageTag was syntactically invalid.
     * @throws UnsupportedOperationException If the operation is not supported.
     */
    default Literal createLiteral(String lexicalForm, String languageTag)
            throws IllegalArgumentException, UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "createLiteral(String,String) not supported");
    }

    /**
     * Create a triple.
     *
     * The returned Triple SHOULD have a {@link Triple#getSubject()} that is
     * equal to the provided subject, a {@link Triple#getPredicate()} that is
     * equal to the provided predicate, and a {@link Triple#getObject()} that is
     * equal to the provided object.
     *
     * @param subject   The IRI or BlankNode that is the subject of the triple
     * @param predicate The IRI that is the predicate of the triple
     * @param object    The IRI, BlankNode or Literal that is the object of the triple
     * @return The created Triple
     * @throws IllegalArgumentException      If any of the provided arguments are not acceptable, e.g.
     *                                       because a Literal has a lexicalForm that is too large for an
     *                                       underlying storage.
     * @throws UnsupportedOperationException If the operation is not supported.
     */
    default Triple createTriple(BlankNodeOrIRI subject, IRI predicate,
                                RDFTerm object) throws IllegalArgumentException,
            UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "createTriple(BlankNodeOrIRI,IRI,RDFTerm) not supported");
    }

    /**
     * Create a quad.
     * <p>
     * The returned Quad SHOULD have a
     * {@link Quad#getGraphName()} that is equal to the provided graphName, a
     * {@link Quad#getSubject()} that is
     * equal to the provided subject, a {@link Quad#getPredicate()} that is
     * equal to the provided predicate, and a {@link Quad#getObject()} that is
     * equal to the provided object.
     *
     * @param graphName The IRI or BlankNode that this quad belongs to, or <code>null</code> for the default graph
     * @param subject   The IRI or BlankNode that is the subject of the quad
     * @param predicate The IRI that is the predicate of the quad
     * @param object    The IRI, BlankNode or Literal that is the object of the quad
     * @return The created Quad
     * @throws IllegalArgumentException      If any of the provided arguments are not acceptable, e.g.
     *                                       because a Literal has a lexicalForm that is too large for an
     *                                       underlying storage.
     * @throws UnsupportedOperationException If the operation is not supported.
     */
    default Quad createQuad(BlankNodeOrIRI graphName, BlankNodeOrIRI subject, IRI predicate,
                                RDFTerm object) throws IllegalArgumentException,
            UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "createTriple(BlankNodeOrIRI,IRI,RDFTerm) not supported");
    }

}
