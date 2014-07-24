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

import java.util.Optional;

/**
 * An RDF-1.1 Literal, as defined by <a href=
 * "http://www.w3.org/TR/rdf11-concepts/#section-Graph-Literal" >RDF-1.1
 * Concepts and Abstract Syntax</a>, a W3C Recommendation published on 25
 * February 2014
 */
public interface Literal extends RDFTerm {

    /**
     * The lexical form of this literal, represented by a <a
     * href="http://www.unicode.org/versions/latest/">Unicode string</a>.
     *
     * @return The lexical form of this literal.
     * @see <a
     * href="http://www.w3.org/TR/rdf11-concepts/#dfn-lexical-form">RDF-1.1
     * Literal lexical form</a>
     */
    String getLexicalForm();

    /**
     * The IRI identifying the datatype that determines how the lexical form
     * maps to a literal value.
     *
     * @return The datatype IRI for this literal.
     * @see <a
     * href="http://www.w3.org/TR/rdf11-concepts/#dfn-datatype-iri">RDF-1.1
     * Literal datatype IRI</a>
     */
    IRI getDatatype();

    /**
     * If and only if the datatype IRI is <a
     * href="http://www.w3.org/1999/02/22-rdf-syntax-ns#langString"
     * >http://www.w3.org/1999/02/22-rdf-syntax-ns#langString</a>, the language
     * tag for this Literal is a non-empty language tag as defined by <a
     * href="http://tools.ietf.org/html/bcp47">BCP47</a>.<br>
     * If the datatype IRI is not <a
     * href="http://www.w3.org/1999/02/22-rdf-syntax-ns#langString"
     * >http://www.w3.org/1999/02/22-rdf-syntax-ns#langString</a>, this method
     * must return {@link Optional#empty()}.
     *
     * @return The {@link Optional} language tag for this literal. If
     * {@link Optional#isPresent()} returns true, the value returned by
     * {@link Optional#get()} must be a non-empty string conforming to
     * BCP47.
     * @see <a
     * href="http://www.w3.org/TR/rdf11-concepts/#dfn-language-tag">RDF-1.1
     * Literal language tag</a>
     */
    Optional<String> getLanguageTag();

}
