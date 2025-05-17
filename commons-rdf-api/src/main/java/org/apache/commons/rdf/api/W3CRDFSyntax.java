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

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * W3C RDF 1.1 serialization syntax.
 * <p>
 * This defines the W3C standardized RDF 1.1 syntaxes like {@link #TURTLE} and {@link #JSONLD}. Note the existence of other RDF syntaxes that are not included
 * here, e.g. <a href="http://www.w3.org/TeamSubmission/n3/">N3</a> and <a href="https://en.wikipedia.org/wiki/TriX_%28syntax%29">TriX</a>.
 * </p>
 * <p>
 * This class is package-protected, its static constants are exposed through {@link RDFSyntax}.
 * </p>
 *
 * @see RDFSyntax#w3cSyntaxes()
 * @see <a href="https://www.w3.org/TR/rdf11-primer/#section-graph-syntax">RDF 1.1 Primer</a>
 * @see org.apache.commons.rdf.experimental.RDFParser
 */
class W3CRDFSyntax implements RDFSyntax {

    /**
     * IRI representing a <a href="https://www.w3.org/ns/formats/">W3C RDF format</a>.
     */
    private static final class FormatIRI implements IRI {

        private static final String BASE = "http://www.w3.org/ns/formats/";
        private final String iriString;

        private FormatIRI(final String format) {
            this.iriString = BASE + Objects.requireNonNull(format, "format");
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || !(obj instanceof IRI)) {
                return false;
            }
            final IRI other = (IRI) obj;
            return getIRIString().equals(other.getIRIString());
        }

        @Override
        public String getIRIString() {
            return iriString;
        }

        @Override
        public int hashCode() {
            return getIRIString().hashCode();
        }

        @Override
        public String ntriplesString() {
            return "<" + getIRIString() + ">";
        }

        @Override
        public String toString() {
            return ntriplesString();
        }
    }

    static final RDFSyntax JSONLD;
    static final RDFSyntax TURTLE;
    static final RDFSyntax NQUADS;
    static final RDFSyntax NTRIPLES;
    static final RDFSyntax RDFA;
    static final RDFSyntax RDFXML;
    static final RDFSyntax TRIG;
    static final Set<RDFSyntax> SYNTAXES;
    static {
        // Initialize within static block to avoid inserting nulls
        JSONLD = new W3CRDFSyntax("JSON-LD", "JSON-LD 1.0", "application/ld+json", true, ".jsonld");
        TURTLE = new W3CRDFSyntax("Turtle", "RDF 1.1 Turtle", "text/turtle", false, ".ttl");
        NQUADS = new W3CRDFSyntax("N-Quads", "RDF 1.1 N-Quads", "application/n-quads", true, ".nq");
        NTRIPLES = new W3CRDFSyntax("N-Triples", "RDF 1.1 N-Triples", "application/n-triples", false, ".nt");
        RDFXML = new W3CRDFSyntax("RDF_XML", "RDF 1.1 XML Syntax", "application/rdf+xml", false, ".rdf");
        TRIG = new W3CRDFSyntax("TriG", "RDF 1.1 TriG", "application/trig", true, ".trig");
        RDFA = new W3CRDFSyntax("RDFa", "HTML+RDFa 1.1", new LinkedHashSet<>(Arrays.asList("text/html", "application/xhtml+xml")), false, ".html", ".xhtml");
        SYNTAXES = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(JSONLD, NQUADS, NTRIPLES, RDFA, RDFXML, TRIG, TURTLE)));
    }
    private final String title;
    private final LinkedHashSet<String> mediaTypes;
    private final LinkedHashSet<String> fileExtensions;
    private final boolean supportsDataset;
    private final String name;
    private final IRI iri;

    private W3CRDFSyntax(final String name, final String title, final LinkedHashSet<String> mediaTypes, final boolean supportsDataset,
            final String... fileExtension) {
        this.name = name;
        this.title = title;
        this.mediaTypes = mediaTypes.stream().map(s -> s.toLowerCase(Locale.ROOT)).collect(Collectors.toCollection(LinkedHashSet::new));
        this.fileExtensions = Stream.of(fileExtension).map(s -> s.toLowerCase(Locale.ROOT)).collect(Collectors.toCollection(LinkedHashSet::new));
        this.supportsDataset = supportsDataset;
        this.iri = new FormatIRI(name);
    }

    private W3CRDFSyntax(final String name, final String title, final String mediaType, final boolean supportsDataset, final String... fileExtension) {
        this(name, title, new LinkedHashSet<>(Arrays.asList(mediaType)), supportsDataset, fileExtension);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof W3CRDFSyntax)) {
            return false;
        }
        W3CRDFSyntax other = (W3CRDFSyntax) obj;
        return Objects.equals(mediaTypes, other.mediaTypes);
    }

    /**
     * {@inheritDoc}
     * <p>
     * {@link W3CRDFSyntax} always defines file extensions in lower case, so {@link String#toLowerCase(Locale)} need not be called.
     * </p>
     */
    @Override
    public String fileExtension() {
        return fileExtensions.iterator().next();
    }

    @Override
    public Set<String> fileExtensions() {
        return fileExtensions;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mediaTypes);
    }

    @Override
    public IRI iri() {
        return iri;
    }

    /**
     * {@inheritDoc}
     * <p>
     * {@link W3CRDFSyntax} always defines media type in lower case, so {@link String#toLowerCase(Locale)} need not be called.
     * </p>
     */
    @Override
    public String mediaType() {
        return mediaTypes.iterator().next();
    }

    @Override
    public Set<String> mediaTypes() {
        return mediaTypes;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean supportsDataset() {
        return supportsDataset;
    }

    @Override
    public String title() {
        return title;
    }

    @Override
    public String toString() {
        return title;
    }
}
