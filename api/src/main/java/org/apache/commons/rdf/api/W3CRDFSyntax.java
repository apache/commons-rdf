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
import java.util.Set;

/**
 * W3C RDF 1.1 serialization syntax.
 * <p>
 * This defines the W3C standardized RDF 1.1 syntaxes like {@link #TURTLE} and
 * {@link #JSONLD}. Note the existence of other RDF syntaxes that are not
 * included here, e.g. <a href="http://www.w3.org/TeamSubmission/n3/">N3</a> and
 * <a href="https://en.wikipedia.org/wiki/TriX_%28syntax%29">TriX</a>.
 * <p>
 * This class is package-protected, its static constants are exposed through
 * {@link RDFSyntax}.
 * 
 * @see RDFSyntax#w3cSyntaxes()
 * @see <a href="https://www.w3.org/TR/rdf11-primer/#section-graph-syntax">RDF
 *      1.1 Primer</a>
 * @see org.apache.commons.rdf.experimental.RDFParser
 */
class W3CRDFSyntax implements RDFSyntax {
    
    /**
     * IRI representing a <a href="https://www.w3.org/ns/formats/">W3C RDF
     * format</a>.
     */
    private static final class FormatIRI implements IRI {
        private static final String BASE = "http://www.w3.org/ns/formats/";
        private final String format;
    
        private FormatIRI(final String format) {
            this.format = format;
        }
    
        @Override
        public String getIRIString() {
            return BASE + format;
        }
    
        @Override
        public String ntriplesString() {
            return "<" + getIRIString() + ">";
        }
    
        @Override
        public String toString() {
            return ntriplesString();
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
        public int hashCode() {
            return getIRIString().hashCode();
        }
    }

    
    static final RDFSyntax JSONLD;
    static final RDFSyntax TURTLE; 
    static final RDFSyntax NQUADS;
    static final RDFSyntax NTRIPLES; 
    static final RDFSyntax RDFA; 
    static final RDFSyntax RDFXML;
    static final RDFSyntax TRIG;
    static final Set<RDFSyntax> syntaxes;
    
    static {
        // Initialize within static block to avoid inserting nulls
        JSONLD = new W3CRDFSyntax("JSON-LD", "JSON-LD 1.0", "application/ld+json", ".jsonld", true);
        TURTLE = new W3CRDFSyntax("Turtle", "RDF 1.1 Turtle", "text/turtle", ".ttl", false);
        NQUADS = new W3CRDFSyntax("N-Quads", "RDF 1.1 N-Quads", "application/n-quads", ".nq", true);
        NTRIPLES = new W3CRDFSyntax("N-Triples", "RDF 1.1 N-Triples", "application/n-triples", ".nt", false);
        RDFXML = new W3CRDFSyntax("RDF_XML", "RDF 1.1 XML Syntax", "application/rdf+xml", ".rdf", false);
        TRIG = new W3CRDFSyntax("TriG", "RDF 1.1 TriG", "application/trig", ".trig", true);        
        RDFA = new W3CRDFSyntax("RDFa", "HTML+RDFa 1.1", "text/html", ".html", false) {
            private Set<String> types = Collections.unmodifiableSet(new LinkedHashSet<>(
                    Arrays.asList("text/html", "application/xhtml+xml")));
            private Set<String> extensions = Collections.unmodifiableSet(new LinkedHashSet<>(
                            Arrays.asList(".html", ".xhtml")));
            @Override
            public Set<String> mediaTypes() {
                return types;
            }
            @Override
            public Set<String> fileExtensions() {
                return extensions;
            }
        };
        syntaxes = Collections.unmodifiableSet(new LinkedHashSet<>(
                Arrays.asList(JSONLD, NQUADS, NTRIPLES, RDFA, RDFXML, TRIG, TURTLE)));
    }
    
    private final String title;

    private final String mediaType;

    private final String fileExtension;
    
    private final boolean supportsDataset;

    private final String name;
    
    private final IRI iri;

    private W3CRDFSyntax(String name, String title, String mediaType, String fileExtension, boolean supportsDataset) {
        this.name = name;
        this.title = title;
        this.mediaType = mediaType.toLowerCase(Locale.ROOT);
        this.fileExtension = fileExtension.toLowerCase(Locale.ROOT);
        this.supportsDataset = supportsDataset;
        this.iri = new FormatIRI(name);
    }

    /**
     * {@inheritDoc}
     * <p>
     * {@link W3CRDFSyntax} always defines media type in lower case, so 
     * {@link String#toLowerCase(Locale)} need not be called.
     * 
     */
    @Override
    public String getmediaType() {
        return mediaType;
    }

    /**
     * {@inheritDoc}
     * <p>
     * {@link W3CRDFSyntax} always defines file extensions in lower case, so
     * {@link String#toLowerCase(Locale)} need not be called.
     * 
     */
    @Override
    public String getfileExtension() {
        return fileExtension;
    }

    @Override
    public boolean supportsDataset() {
        return supportsDataset;
    }

    @Override
    public String gettitle() {
        return title;
    }

    @Override
    public String getname() {
        return name;
    }
    
    @Override
    public IRI getiri() {
        return iri;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof RDFSyntax)) {
            return false;
        }
        RDFSyntax other = (RDFSyntax) obj;
        return mediaType.equalsIgnoreCase(other.getmediaType());
    }

    @Override
    public int hashCode() {
        return mediaType.hashCode();
    }

    @Override
    public String toString() {
        return title;
    }

}