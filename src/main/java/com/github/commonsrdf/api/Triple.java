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

/**
 * An <a href= "http://www.w3.org/TR/rdf11-concepts/#dfn-rdf-triple" >RDF-1.1
 * Triple</a>, as defined by <a href= "http://www.w3.org/TR/rdf11-concepts/"
 * >RDF-1.1 Concepts and Abstract Syntax</a>, a W3C Recommendation published on
 * 25 February 2014.<br>
 *
 * @see <a href= "http://www.w3.org/TR/rdf11-concepts/#dfn-rdf-triple" >RDF-1.1
 * Triple</a>
 */
public interface Triple {

    /**
     * The subject of this triple, which may be either a {@link BlankNode} or an
     * {@link IRI}, which are represented in Commons RDF by the interface
     * {@link BlankNodeOrIRI}.
     *
     * @return The subject {@link BlankNodeOrIRI} of this triple.
     * @see <a href="http://www.w3.org/TR/rdf11-concepts/#dfn-subject">RDF-1.1
     * Triple subject</a>
     */
    BlankNodeOrIRI getSubject();

    /**
     * The predicate {@link IRI} of this triple.
     *
     * @return The predicate {@link IRI} of this triple.
     * @see <a href="http://www.w3.org/TR/rdf11-concepts/#dfn-predicate">RDF-1.1
     * Triple predicate</a>
     */
    IRI getPredicate();

    /**
     * The object of this triple, which may be either a {@link BlankNode}, an
     * {@link IRI}, or a {@link Literal}, which are represented in Commons RDF
     * by the interface {@link RDFTerm}.
     *
     * @return The object {@link RDFTerm} of this triple.
     * @see <a href="http://www.w3.org/TR/rdf11-concepts/#dfn-object">RDF-1.1
     * Triple object</a>
     */
    RDFTerm getObject();

}
