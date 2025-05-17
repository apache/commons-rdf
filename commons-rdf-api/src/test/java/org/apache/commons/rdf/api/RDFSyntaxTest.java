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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

public class RDFSyntaxTest {

    @Test
    public void testByFileExtension() throws Exception {
        assertEquals(RDFSyntax.JSONLD, RDFSyntax.byFileExtension(".jsonld").get());
        assertEquals(RDFSyntax.NQUADS, RDFSyntax.byFileExtension(".nq").get());
        assertEquals(RDFSyntax.NTRIPLES, RDFSyntax.byFileExtension(".nt").get());
        assertEquals(RDFSyntax.RDFA, RDFSyntax.byFileExtension(".html").get());
        assertEquals(RDFSyntax.RDFXML, RDFSyntax.byFileExtension(".rdf").get());
        assertEquals(RDFSyntax.TRIG, RDFSyntax.byFileExtension(".trig").get());
        assertEquals(RDFSyntax.TURTLE, RDFSyntax.byFileExtension(".ttl").get());
    }

    @Test
    public void testByFileExtensionFailsWithoutDot() throws Exception {
        assertEquals(Optional.empty(), RDFSyntax.byFileExtension("rdf"));
    }

    @Test
    public void testByFileExtensionLowerCase() throws Exception {
        assertEquals(RDFSyntax.TURTLE, RDFSyntax.byFileExtension(".TtL").get());
    }

    @Test
    public void testByFileExtensionUnknown() throws Exception {
        assertEquals(Optional.empty(), RDFSyntax.byFileExtension(".tar"));
    }

    @Test
    public void testByMediaType() throws Exception {
        assertEquals(RDFSyntax.JSONLD, RDFSyntax.byMediaType("application/ld+json").get());
        assertEquals(RDFSyntax.NQUADS, RDFSyntax.byMediaType("application/n-quads").get());
        assertEquals(RDFSyntax.NTRIPLES, RDFSyntax.byMediaType("application/n-triples").get());
        assertEquals(RDFSyntax.RDFA, RDFSyntax.byMediaType("text/html").get());
        assertEquals(RDFSyntax.RDFA, RDFSyntax.byMediaType("application/xhtml+xml").get());
        assertEquals(RDFSyntax.RDFXML, RDFSyntax.byMediaType("application/rdf+xml").get());
        assertEquals(RDFSyntax.TRIG, RDFSyntax.byMediaType("application/trig").get());
        assertEquals(RDFSyntax.TURTLE, RDFSyntax.byMediaType("text/turtle").get());
    }

    @Test
    public void testByMediaTypeContentType() throws Exception {
        assertEquals(RDFSyntax.TURTLE, RDFSyntax.byMediaType("text/turtle; charset=\"UTF-8\"").get());
        assertEquals(RDFSyntax.TURTLE, RDFSyntax.byMediaType("text/turtle ; charset=\"UTF-8\"").get());
        // That's a Content-Type, not media type; we won't split by ","
        assertEquals(Optional.empty(), RDFSyntax.byMediaType("text/turtle, text/plain"));
        // no trimming will be done
        assertEquals(Optional.empty(), RDFSyntax.byMediaType(" text/turtle"));
    }

    @Test
    public void testByMediaTypeLowerCase() throws Exception {
        assertEquals(RDFSyntax.JSONLD, RDFSyntax.byMediaType("APPLICATION/ld+JSON").get());
    }

    @Test
    public void testByMediaTypeUnknown() throws Exception {
        assertEquals(Optional.empty(), RDFSyntax.byMediaType("application/octet-stream"));
    }

    @Test
    public void testByName() throws Exception {
        for (final RDFSyntax s : RDFSyntax.w3cSyntaxes()) {
            assertEquals(s, RDFSyntax.byName(s.name()).get());
        }
    }

    @Test
    public void testFileExtension() throws Exception {
        assertEquals(".jsonld", RDFSyntax.JSONLD.fileExtension());
        assertEquals(".nq", RDFSyntax.NQUADS.fileExtension());
        assertEquals(".nt", RDFSyntax.NTRIPLES.fileExtension());
        assertEquals(".html", RDFSyntax.RDFA.fileExtension());
        assertEquals(".rdf", RDFSyntax.RDFXML.fileExtension());
        assertEquals(".trig", RDFSyntax.TRIG.fileExtension());
        assertEquals(".ttl", RDFSyntax.TURTLE.fileExtension());
    }

    @Test
    public void testFileExtensions() throws Exception {
        assertTrue(RDFSyntax.JSONLD.fileExtensions().contains(".jsonld"));
        assertTrue(RDFSyntax.NQUADS.fileExtensions().contains(".nq"));
        assertTrue(RDFSyntax.NTRIPLES.fileExtensions().contains(".nt"));
        assertTrue(RDFSyntax.RDFA.fileExtensions().contains(".html"));
        assertTrue(RDFSyntax.RDFA.fileExtensions().contains(".xhtml"));
        assertTrue(RDFSyntax.RDFXML.fileExtensions().contains(".rdf"));
        assertTrue(RDFSyntax.TRIG.fileExtensions().contains(".trig"));
        assertTrue(RDFSyntax.TURTLE.fileExtensions().contains(".ttl"));
    }

    @Test
    public void testIri() throws Exception {
        assertEquals("<http://www.w3.org/ns/formats/JSON-LD>", RDFSyntax.JSONLD.iri().toString());
        assertEquals("<http://www.w3.org/ns/formats/N-Quads>", RDFSyntax.NQUADS.iri().toString());
        assertEquals("<http://www.w3.org/ns/formats/N-Triples>", RDFSyntax.NTRIPLES.iri().toString());
        assertEquals("<http://www.w3.org/ns/formats/RDFa>", RDFSyntax.RDFA.iri().toString());
        assertEquals("<http://www.w3.org/ns/formats/RDF_XML>", RDFSyntax.RDFXML.iri().toString());
        assertEquals("<http://www.w3.org/ns/formats/TriG>", RDFSyntax.TRIG.iri().toString());
        assertEquals("<http://www.w3.org/ns/formats/Turtle>", RDFSyntax.TURTLE.iri().toString());
        assertEquals("<http://www.w3.org/ns/formats/JSON-LD>", RDFSyntax.JSONLD.iri().toString());
    }

    @Test
    public void testMediaType() throws Exception {
        assertEquals("application/ld+json", RDFSyntax.JSONLD.mediaType());
        assertEquals("application/n-quads", RDFSyntax.NQUADS.mediaType());
        assertEquals("application/n-triples", RDFSyntax.NTRIPLES.mediaType());
        assertEquals("text/html", RDFSyntax.RDFA.mediaType());
        assertEquals("application/rdf+xml", RDFSyntax.RDFXML.mediaType());
        assertEquals("application/trig", RDFSyntax.TRIG.mediaType());
        assertEquals("text/turtle", RDFSyntax.TURTLE.mediaType());
    }

    @Test
    public void testMediaTypes() throws Exception {
        assertTrue(RDFSyntax.JSONLD.mediaTypes().contains("application/ld+json"));
        assertTrue(RDFSyntax.NQUADS.mediaTypes().contains("application/n-quads"));
        assertTrue(RDFSyntax.NTRIPLES.mediaTypes().contains("application/n-triples"));
        assertTrue(RDFSyntax.RDFA.mediaTypes().contains("text/html"));
        assertTrue(RDFSyntax.RDFA.mediaTypes().contains("application/xhtml+xml"));
        assertTrue(RDFSyntax.RDFXML.mediaTypes().contains("application/rdf+xml"));
        assertTrue(RDFSyntax.TRIG.mediaTypes().contains("application/trig"));
        assertTrue(RDFSyntax.TURTLE.mediaTypes().contains("text/turtle"));
    }

    @Test
    public void testString() throws Exception {
        assertEquals("JSON-LD 1.0", RDFSyntax.JSONLD.toString());
        assertEquals("RDF 1.1 Turtle", RDFSyntax.TURTLE.toString());
    }

}
