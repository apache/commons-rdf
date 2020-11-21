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

import java.util.Collections;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/**
 * An RDF syntax, e.g. as used for parsing and writing RDF.
 * <p>
 * An RDF syntax is uniquely identified by its {@link #mediaType()}, and has a
 * suggested {@link #fileExtension()}.
 * <p>
 * Some of the RDF syntaxes may {@link #supportsDataset()}, meaning they can
 * represent {@link Quad}s.
 * <p>
 * An enumeration of the official RDF 1.1 syntaxes is available in
 * {@link W3CRDFSyntax} - for convenience they are also accessible
 * as constants here, e.g. <code>RDFSyntax.JSONLD</code>.
 *
 */
public interface RDFSyntax {

    /**
     * JSON-LD 1.0
     *
     * @see <a href=
     *      "https://www.w3.org/TR/json-ld/">https://www.w3.org/TR/json-ld/</a>
     *
     */
    RDFSyntax JSONLD = W3CRDFSyntax.JSONLD;

    /**
     * RDF 1.1 Turtle
     *
     * @see <a href=
     *      "https://www.w3.org/TR/turtle/">https://www.w3.org/TR/turtle/</a>
     *
     */
    RDFSyntax TURTLE = W3CRDFSyntax.TURTLE;

    /**
     * RDF 1.1 N-Quads
     *
     * @see <a href=
     *      "https://www.w3.org/TR/n-quads/">https://www.w3.org/TR/n-quads/</a>
     */
    RDFSyntax NQUADS = W3CRDFSyntax.NQUADS;

    /**
     * RDF 1.1 N-Triples
     *
     * @see <a href=
     *      "https://www.w3.org/TR/n-triples/">https://www.w3.org/TR/n-triples/</a>
     */
    RDFSyntax NTRIPLES = W3CRDFSyntax.NTRIPLES;

    /**
     * HTML+RDFa 1.1 and XHTML+RDFa 1.1
     *
     * @see <a href=
     *      "https://www.w3.org/TR/html-rdfa/">https://www.w3.org/TR/html-rdfa/</a>
     * @see <a href=
     *      "https://www.w3.org/TR/xhtml-rdfa/">https://www.w3.org/TR/xhtml-rdfa/</a>
     */
    RDFSyntax RDFA = W3CRDFSyntax.RDFA;

    /**
     * RDF 1.1 XML Syntax
     *
     * @see <a href=
     *      "https://www.w3.org/TR/rdf-syntax-grammar/">https://www.w3.org/TR/rdf-syntax-grammar/</a>
     */
    RDFSyntax RDFXML = W3CRDFSyntax.RDFXML;

    /**
     * RDF 1.1 TriG
     *
     * @see <a href=
     *      "https://www.w3.org/TR/trig/">https://www.w3.org/TR/trig/</a>
     */
    RDFSyntax TRIG = W3CRDFSyntax.TRIG;

    /**
     * A short name of the RDF Syntax e.g. <code>JSONLD</code>.
     * <p>
     * The name is specific to Commons RDF and carries no particular meaning.
     *
     * @return Short name for RDF syntax
     */
    String name();

    /**
     * The title of the RDF Syntax.
     * <p>
     * This is generally the title of the corresponding standard,
     * e.g. <em>RDF 1.1 Turtle</em>.
     *
     * @return Title of RDF Syntax
     */
    String title();

    /**
     * The <a href="https://tools.ietf.org/html/rfc2046">IANA media type</a> for
     * the RDF syntax.
     * <p>
     * The media type can be used as part of <code>Content-Type</code> and
     * <code>Accept</code> for <em>content negotiation</em> in the
     * <a href="https://tools.ietf.org/html/rfc7231#section-3.1.1.1">HTTP
     * protocol</a>.
     *
     * @return The registered media type of the RDF Syntax
     */
    String mediaType();

    /**
     * Set of <a href="https://tools.ietf.org/html/rfc2046">IANA media types</a> that
     * covers this RDF syntax, including any non-official media types.
     * <p>
     * The media type can be used as part of <code>Content-Type</code> and
     * <code>Accept</code> for <em>content negotiation</em> in the
     * <a href="https://tools.ietf.org/html/rfc7231#section-3.1.1.1">HTTP
     * protocol</a>.
     * <p>
     * The returned Set MUST include the value {@link #mediaType()}; this is the
     * behavior of the default implementation.
     *
     * @return The media types corresponding to the RDF Syntax
     */
    default Set<String> mediaTypes() {
        return Collections.singleton(mediaType());
    }

    /**
     * The <a href="https://tools.ietf.org/html/rfc2046">IANA-registered</a>
     * file extension.
     * <p>
     * The file extension includes the leading period, e.g. <code>.jsonld</code>
     *
     * @return The registered file extension of the RDF Syntax
     */
    String fileExtension();

    /**
     * Set of file extensions for this RDF syntax, including any non-official extensions.
     * <p>
     * The file extension includes the leading period, e.g. <code>.jsonld</code>
     * <p>
     * The returned Set MUST include the value from {@link #fileExtension()}; this is
     * the behavior of the default implementation.
     *
     * @return The file extensions corresponding to the RDF Syntax
     */
    default Set<String> fileExtensions() {
        return Collections.singleton(fileExtension());
    }

    /**
     * Indicate if this RDF syntax supports
     * <a href="https://www.w3.org/TR/rdf11-concepts/#section-dataset">RDF
     * Datasets</a>.
     *
     * @return true if this RDF Syntax supports datasets; false otherwise
     */
    boolean supportsDataset();

    /**
     * Return the {@link IRI} that <em>identifies</em> the RDF syntax.
     * <p>
     * Note that the identifying IRI is generally distinct from the IRI of the
     * document that <em>specifies</em> the RDF syntax.
     *
     * @return Identifying IRI, e.g.
     *         <code>http://www.w3.org/ns/formats/JSON-LD</code>
     */
    IRI iri();

    /**
     * Compare this RDFSyntax with another object.
     * <p>
     * Two {@link RDFSyntax}es are considered equal if their
     * {@link #mediaType()}s are equal when compared as lower case strings
     * according to {@link String#toLowerCase(Locale)} with the locale
     * {@link Locale#ROOT}.
     *
     * @param obj the object with which to compare
     * @return true if this object is the same as the obj argument; false otherwise
     */
    @Override
    boolean equals(Object obj);

    /**
     * The hash code of an RDFSyntax is equivalent to the hash code
     * of the {@link #mediaType()} in lower case according to
     * {@link String#toLowerCase(Locale)} with the locale
     * {@link Locale#ROOT}.
     *
     * @return Hash code of RDFSyntax
     */
    @Override
    int hashCode();

    /**
     * Return the RDF 1.1 serialization syntaxes.
     * <p>
     * This lists the W3C standardized RDF 1.1 syntaxes like {@link #TURTLE} and
     * {@link #JSONLD}. Note the existence of other RDF syntaxes that are not
     * included here, e.g. <a href="http://www.w3.org/TeamSubmission/n3/">N3</a> and
     * <a href="https://en.wikipedia.org/wiki/TriX_%28syntax%29">TriX</a>.
     * <p>
     * The syntaxes returned only support the {@link #mediaType()}
     * and {@link #fileExtension()} as defined in the corresponding
     * W3C specification.
     *
     * @return
     *      A set of the official RDF 1.1 {@link RDFSyntax}es.
     *
     * @see <a href="https://www.w3.org/TR/rdf11-primer/#section-graph-syntax">RDF
     *      1.1 Primer</a>
     * @see org.apache.commons.rdf.experimental.RDFParser
     */

    static Set<RDFSyntax> w3cSyntaxes() {
        return W3CRDFSyntax.syntaxes;
    }

    /**
     * Return the RDFSyntax with the specified media type.
     * <p>
     * The <code>mediaType</code> is compared in lower case to all media types
     * supported, therefore it might not be equal to the
     * {@link RDFSyntax#mediaType} of the returned RDFSyntax.
     * <p>
     * If the media type specifies parameters, e.g.
     * <code>text/turtle; charset=ascii</code>, only the part of the string to
     * before <code>;</code> is considered.
     * <p>
     * This method support all syntaxes returned by {@link #w3cSyntaxes()}.
     *
     * @param mediaType
     *            The media type to match
     * @return If {@link Optional#isPresent()}, the {@link RDFSyntax} which has
     *         a matching {@link RDFSyntax#mediaType()}, otherwise
     *         {@link Optional#empty()} indicating that no matching syntax was
     *         found.
     */
    static Optional<RDFSyntax> byMediaType(final String mediaType) {
        final String type = mediaType.toLowerCase(Locale.ROOT).split("\\s*;", 2)[0];
        return w3cSyntaxes().stream().filter(t -> t.mediaTypes().contains(type))
                .findAny();
    }

    /**
     * Return the RDFSyntax with the specified file extension.
     * <p>
     * The <code>fileExtension</code> is compared in lower case to all
     * extensions supported, therefore it might not be equal to the
     * {@link RDFSyntax#fileExtension} of the returned RDFSyntax.
     * <p>
     * This method support all syntaxes returned by {@link #w3cSyntaxes()}.
     *
     * @param fileExtension
     *            The fileExtension to match, starting with <code>.</code>
     * @return If {@link Optional#isPresent()}, the {@link RDFSyntax} which has
     *         a matching {@link RDFSyntax#fileExtension()}, otherwise
     *         {@link Optional#empty()} indicating that no matching file
     *         extension was found.
     */
    static Optional<RDFSyntax> byFileExtension(final String fileExtension) {
        final String ext = fileExtension.toLowerCase(Locale.ROOT);
        return w3cSyntaxes().stream().filter(t -> t.fileExtensions().contains(ext))
                .findAny();
    }

    /**
     * Return the RDFSyntax with the specified {@link #name()}.
     * <p>
     * This method support all syntaxes returned by {@link #w3cSyntaxes()}.
     *
     * @param name
     *            The name to match, , e.g. <code>"JSONLD"</code>
     * @return If {@link Optional#isPresent()}, the {@link RDFSyntax} which has
     *         a matching {@link RDFSyntax#name()}, otherwise
     *         {@link Optional#empty()} indicating that no matching name was found.
     */
    static Optional<RDFSyntax> byName(final String name) {
        return w3cSyntaxes().stream().filter(t -> t.name().equals(name)).findAny();
    }


}
